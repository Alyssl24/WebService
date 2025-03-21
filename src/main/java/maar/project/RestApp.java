package maar.project;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import maar.project.drinks.*;
import maar.project.meal.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Path("/recipe")
public class RestApp {

    private static final String API_URL = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "c63c1b50";
    private static final String APP_KEY = "9435486d5c699d9ea4b5d3f86ff0a035";

    private final Client client = ClientBuilder.newClient();

    private static final Random RANDOM = new Random();

    public static Recipe convertJsonToRecette(String jsonResponse) {
        Recipe recette = null;
        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject rootJson = jsonReader.readObject();
            JsonArray hits = rootJson.getJsonArray("hits");

            if (hits == null || hits.isEmpty()) {
                return null; // Aucune recette trouvée
            }

            // Sélection random d'une recette parmi les hits
            int randomIndex = RANDOM.nextInt(hits.size());
            JsonObject recipeJson = hits.getJsonObject(randomIndex).getJsonObject("recipe");

            // Récupération des informations principales
            String uri = getSafeJsonString(recipeJson, "uri");
            String label = getSafeJsonString(recipeJson, "label");
            String image = getSafeJsonString(recipeJson, "image");
            String url = getSafeJsonString(recipeJson, "url");

            // Gestion des ingrédients
            List<Ingredient> ingredients = new ArrayList<>();
            JsonArray ingredientsDetailArray = recipeJson.getJsonArray("ingredients");
            if (ingredientsDetailArray != null) {
                for (JsonValue jsonValue : ingredientsDetailArray) {
                    if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                        JsonObject ingredientJson = jsonValue.asJsonObject();
                        String texteComplet = getSafeJsonString(ingredientJson, "text");
                        String nomPur = getSafeJsonString(ingredientJson, "food");
                        String quantite = ingredientJson.containsKey("quantity")
                                ? ingredientJson.getJsonNumber("quantity").toString() +
                                (ingredientJson.containsKey("measure") ? " " + getSafeJsonString(ingredientJson, "measure") : "")
                                : "";
                        String imageIngredient = getSafeJsonString(ingredientJson, "image");
                        ingredients.add(new Ingredient(texteComplet, nomPur, quantite, imageIngredient));
                    }
                }
            }

            // Gestion des types (cuisine, repas, plat) avec vérification du type
            MealTypes typesRepas = new MealTypes(extractJsonArrayAsList(recipeJson, "mealType"));
            DishTypes typesPlat = new DishTypes(extractJsonArrayAsList(recipeJson, "dishType"));
            List<String> typesCuisine = extractJsonArrayAsList(recipeJson, "cuisineType");

            // Conversion du temps total en format ISO 8601
            String timeFormatted = "PT30M"; // Valeur par défaut
            if (recipeJson.containsKey("totalTime") && recipeJson.get("totalTime").getValueType() == JsonValue.ValueType.NUMBER) {
                int totalMinutes = recipeJson.getJsonNumber("totalTime").intValue();
                timeFormatted = Duration.ofMinutes(totalMinutes).toString();
            }

            // Récupération des allergènes à partir de `healthLabels`
            List<String> healthLabels = extractJsonArrayAsList(recipeJson, "healthLabels");
            String allergenes = String.join(", ", healthLabels); // Convertir la liste en une seule chaîne

            // Création de l'objet TypeDetails
            RecipeDetails typeDetails = new RecipeDetails(typesRepas, typesPlat, typesCuisine);

            // Construction de l'objet Recette
            recette = new Recipe(
                    uri,
                    label,
                    typeDetails,
                    timeFormatted,
                    image,
                    url,
                    ingredients,
                    allergenes, // Ajout des allergènes
                    BigDecimal.valueOf(recipeJson.containsKey("calories")
                            ? recipeJson.getJsonNumber("calories").doubleValue()
                            : 0.0) // Valeur par défaut
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recette;
    }

    @GET
    @Path("/meal/{cuisineType}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getRecipe(@PathParam("cuisineType") String cuisineType) {
        // Vérif 400 : vide
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Le paramètre 'cuisineType' est invalide ou vide.")
                    .build();
        }

        // Construit l'URL
        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;

        return processRecipeResponse(fullUrl, (apiResponse) -> {
            int status = apiResponse.getStatus();

            if (status != 200) {
                String errorMessage = apiResponse.readEntity(String.class);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur API externe (HTTP " + status + ") : " + errorMessage)
                        .build();
            }

            String jsonResponse = apiResponse.readEntity(String.class);
            JsonObject rootJson;
            try (JsonReader reader = Json.createReader(new StringReader(jsonResponse))) {
                rootJson = reader.readObject();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur de lecture JSON : " + e.getMessage())
                        .build();
            }

            // Vérif 400 : cuisineType pas reconnu (aucun hit)
            JsonArray hits = rootJson.getJsonArray("hits");
            if (hits == null || hits.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Aucune recette trouvée pour le type de cuisine : " + cuisineType)
                        .build();
            }

            // Parse recette aléatoire
            Recipe recette = convertJsonToRecette(jsonResponse);

            // Convertit en XML
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Recipe.class);
                StringWriter xmlWriter = new StringWriter();
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(recette, xmlWriter);
                return Response.ok(xmlWriter.toString()).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur lors de la génération du XML : " + e.getMessage())
                        .build();
            }
        });
    }

    private Response processRecipeResponse(String url, Function<Response, Response> parser) {
        try {
            Response apiResponse = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            return parser.apply(apiResponse);

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur interne lors de l'appel à l'API : " + e.getMessage())
                    .build();
        }
    }

    // API BOISSONS PART
    private static final String API_URL_DRINK = "https://www.thecocktaildb.com/api/json/v1/1/";

    @GET
    @Path("/drink")
    @Produces(MediaType.APPLICATION_XML)
    public Response getDrink(@QueryParam("alcoholic") String alcoholic) {
        String filterPath;

        if (alcoholic == null) {
            filterPath = "random.php";
        } else {
            switch (alcoholic) {
                case "true":
                    filterPath = "filter.php?a=Alcoholic";
                    break;
                case "false":
                    filterPath = "filter.php?a=Non_Alcoholic";
                    break;
                default:
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("<error>Le paramètre 'alcoholic' est invalide ou vide.</error>")
                            .build();

            }
        }

        String fullUrl = API_URL_DRINK + filterPath;
        Response apiResponse;
        try {
            apiResponse = client.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>Erreur appel API : " + e.getMessage() + "</error>")
                    .build();
        }

        if (alcoholic == null) {
            String result = processApiResponse(apiResponse, (resp) -> convertJsonToXml(resp.readEntity(String.class)));
            return Response.ok(result).build();
        }

        // Sinon on va chercher les détails d'une boisson
        return processApiResponse(apiResponse, (resp) -> {
            JsonObject rootJson = resp.readEntity(JsonObject.class);
            JsonArray drinksArray = rootJson.getJsonArray("drinks");

            if (drinksArray == null || drinksArray.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("<error>Aucune boisson trouvée.</error>")
                        .build();
            }

            int randomIndex = RANDOM.nextInt(drinksArray.size());
            String drinkId = drinksArray.getJsonObject(randomIndex).getString("idDrink");

            Response detailsResponse = client.target(API_URL_DRINK + "lookup.php?i=" + drinkId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            String xml = convertJsonToXml(detailsResponse.readEntity(String.class));
            return Response.ok(xml).build();
        });
    }


    private String convertJsonToXml(String jsonResponse) {
        try {
            // Lire le JSON
            JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
            JsonObject rootJson = jsonReader.readObject();
            JsonArray drinksArray = rootJson.getJsonArray("drinks");

            if (drinksArray == null || drinksArray.isEmpty()) {
                return "<error>Aucune boisson trouvée.</error>";
            }

            JsonObject drinkJson = drinksArray.getJsonObject(0); // Prendre le premier élément

            DrinkRecipe drinkRecipe = new DrinkRecipe(
                    drinkJson.getString("idDrink"),
                    drinkJson.getString("strDrink"),
                    new DrinkDetails(
                            "Alcoholic".equals(drinkJson.getString("strAlcoholic")), // Convertir en booléen
                            drinkJson.getString("strCategory"),
                            drinkJson.getString("strGlass")
                    ),
                    drinkJson.getString("strDrinkThumb"),
                    extractSyntheticIngredients(drinkJson),
                    extractIngredients(drinkJson),
                    drinkJson.getString("strInstructions")
            );

            JAXBContext jaxbContext = JAXBContext.newInstance(DrinkRecipe.class);
            StringWriter xmlWriter = new StringWriter();
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(drinkRecipe, xmlWriter);

            return xmlWriter.toString();

        } catch (Exception e) {
            return "<error>Erreur lors de la conversion JSON vers XML: " + e.getMessage() + "</error>";
        }
    }

    private SyntheticIngredients extractSyntheticIngredients(JsonObject drinkJson) {
        List<String> syntheticList = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            String ingredientKey = "strIngredient" + i;

            if (drinkJson.containsKey(ingredientKey) && drinkJson.get(ingredientKey) != JsonValue.NULL) {
                String ingredient = drinkJson.getString(ingredientKey).trim();
                if (!ingredient.isEmpty()) {
                    syntheticList.add(ingredient);
                }
            }
        }
        if (syntheticList.isEmpty()) {
            syntheticList.add("Unknown");
        }

        return new SyntheticIngredients(syntheticList);
    }

    private IngredientDetails extractIngredients(JsonObject drinkJson) {
        List<IngredientDrink> ingredients = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {  // Les ingrédients sont numérotés de 1 à 15
            String ingredientKey = "strIngredient" + i;
            String measureKey = "strMeasure" + i;

            if (drinkJson.containsKey(ingredientKey) && drinkJson.get(ingredientKey) != JsonValue.NULL) {
                String ingredient = drinkJson.getString(ingredientKey, "");
                String measure = drinkJson.getString(measureKey, "");
                if (!ingredient.isEmpty()) {
                    ingredients.add(new IngredientDrink(ingredient, ingredient, measure));
                }
            }
        }
        return new IngredientDetails(ingredients);
    }

    /**
     * Récupère une valeur String d'un JsonObject de manière sécurisée.
     */
    private static String getSafeJsonString(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key) && jsonObject.get(key).getValueType() == JsonValue.ValueType.STRING) {
            return jsonObject.getString(key);
        }
        return ""; // Valeur par défaut si la clé est absente ou non string
    }

    /**
     * Récupère une liste de String à partir d'un JsonArray, en s'assurant qu'il ne cause pas d'erreur.
     */
    private static List<String> extractJsonArrayAsList(JsonObject jsonObject, String key) {
        List<String> resultList = new ArrayList<>();
        if (jsonObject.containsKey(key) && jsonObject.get(key).getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray jsonArray = jsonObject.getJsonArray(key);
            for (JsonValue value : jsonArray) {
                if (value.getValueType() == JsonValue.ValueType.STRING) {
                    resultList.add(value.toString().replace("\"", ""));
                }
            }
        }
        return resultList;
    }


    private <T> T processApiResponse(Response response, Function<Response, T> parser) {
        try {
            return parser.apply(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}