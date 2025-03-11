package maar.project;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import maar.project.drinks.*;
import maar.project.recette.Ingredient;
import maar.project.recette.Recette;
import maar.project.recette.TypeDetails;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Path("/recipe")
public class RestApp {

    private static final String API_URL = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "c63c1b50";
    private static final String APP_KEY = "9435486d5c699d9ea4b5d3f86ff0a035";

    private final Client client = ClientBuilder.newClient();

    // Instance statique de Random pour obtenir des index aléatoires
    private static final Random RANDOM = new Random();

    public static Recette convertJsonToRecette(String jsonResponse) {
        Recette recette = null;
        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject rootJson = jsonReader.readObject();
            JsonArray hits = rootJson.getJsonArray("hits");

            // Sélection aléatoire d'une recette parmi les hits
            int randomIndex = RANDOM.nextInt(hits.size());
            JsonObject recipeJson = hits.getJsonObject(randomIndex).getJsonObject("recipe");

            // Récupération du tableau des ingrédients
            JsonArray ingredientsDetailArray = recipeJson.getJsonArray("ingredients");
            List<Ingredient> ingredients = new ArrayList<>();
            if (ingredientsDetailArray != null) {
                for (int i = 0; i < ingredientsDetailArray.size(); i++) {
                    JsonObject ingredientJson = ingredientsDetailArray.getJsonObject(i);
                    String texteComplet = ingredientJson.getString("text", "");
                    String nomPur = ingredientJson.getString("food", "");
                    String quantite = ingredientJson.containsKey("quantity")
                            ? ingredientJson.getJsonNumber("quantity").toString()
                            + (ingredientJson.containsKey("measure") ? " " + ingredientJson.getString("measure") : "")
                            : "";
                    String imageIngredient = ingredientJson.getString("image", "");
                    Ingredient ing = new Ingredient(texteComplet, nomPur, quantite, imageIngredient);
                    ingredients.add(ing);
                }
            }

            // Récupération des types (cuisine, repas, plat)
            List<String> typesCuisine = new ArrayList<>();
            JsonArray typeCuisineArray = recipeJson.getJsonArray("cuisineType");
            if (typeCuisineArray != null) {
                for (int i = 0; i < typeCuisineArray.size(); i++) {
                    JsonValue value = typeCuisineArray.get(i);
                    String s;
                    if (value.getValueType() == JsonValue.ValueType.STRING) {
                        s = ((JsonString) value).getString();
                    } else {
                        s = value.toString();
                        if (s.startsWith("\"") && s.endsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                    }
                    typesCuisine.add(s);
                }
            }

            List<String> typesRepas = new ArrayList<>();
            JsonArray typeRepasArray = recipeJson.getJsonArray("mealType");
            if (typeRepasArray != null) {
                for (int i = 0; i < typeRepasArray.size(); i++) {
                    JsonValue value = typeRepasArray.get(i);
                    String s;
                    if (value.getValueType() == JsonValue.ValueType.STRING) {
                        s = ((JsonString) value).getString();
                    } else {
                        s = value.toString();
                        if (s.startsWith("\"") && s.endsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                    }
                    typesRepas.add(s);
                }
            }

            List<String> typesPlat = new ArrayList<>();
            JsonArray typePlatArray = recipeJson.getJsonArray("dishType");
            if (typePlatArray != null) {
                for (int i = 0; i < typePlatArray.size(); i++) {
                    JsonValue value = typePlatArray.get(i);
                    String s;
                    if (value.getValueType() == JsonValue.ValueType.STRING) {
                        s = ((JsonString) value).getString();
                    } else {
                        s = value.toString();
                        if (s.startsWith("\"") && s.endsWith("\"")) {
                            s = s.substring(1, s.length() - 1);
                        }
                    }
                    typesPlat.add(s);
                }
            }

            // Conversion du temps total (en minutes) en format heure (HH:mm:ss)
            double totalTime = recipeJson.containsKey("totalTime")
                    ? recipeJson.getJsonNumber("totalTime").doubleValue()
                    : 30.0;
            int totalMinutes = (int) totalTime;
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            LocalTime localTime = LocalTime.of(hours, minutes, 0);
            String timeFormatted = localTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

            // Création de l'objet TypeDetails
            TypeDetails typeDetails = new TypeDetails(typesRepas, typesPlat, typesCuisine);

            // Construction de l'objet Recette
            recette = new Recette(
                    recipeJson.getString("uri"),
                    recipeJson.getString("label"),
                    typeDetails,
                    timeFormatted,
                    recipeJson.getString("image"),
                    recipeJson.getString("url"),
                    ingredients,
                    "", // allergènes sous forme de chaîne vide
                    BigDecimal.valueOf(recipeJson.getJsonNumber("calories").doubleValue())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recette;
    }

    @GET
    @Path("/meal/{cuisineType: .*}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getRecipe(@PathParam("cuisineType") String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Le paramètre 'cuisineType' est invalide ou vide.")
                    .build();
        }

        // Construction de l'URL pour appeler l'API
        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;
        Response apiResponse;
        try {
            apiResponse = client.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de l'appel à l'API externe: " + e.getMessage())
                    .build();
        }

        if (apiResponse.getStatus() != 200) {
            String errorMessage = apiResponse.readEntity(String.class);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur API externe (HTTP " + apiResponse.getStatus() + "): " + errorMessage)
                    .build();
        }

        String jsonResponse = apiResponse.readEntity(String.class);

        // Vérification rapide que l'API a retourné au moins une recette
        try (JsonReader jr = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject rootJson = jr.readObject();
            JsonArray hits = rootJson.getJsonArray("hits");
            if (hits == null || hits.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur API externe : aucune recette trouvée pour le cuisineType : " + cuisineType)
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors du traitement du JSON retourné par l'API externe: " + e.getMessage())
                    .build();
        }

        Recette recette = convertJsonToRecette(jsonResponse);

        // Génération du XML à partir de l'objet Recette
        StringWriter xmlWriter = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Recette.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(recette, xmlWriter);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la génération du XML: " + e.getMessage())
                    .build();
        }
        return Response.ok(xmlWriter.toString()).build();
    }


    // API BOISSONS PART
    private static final String API_URL_DRINK = "https://www.thecocktaildb.com/api/json/v1/1/";

    @GET
    @Path("/drink")
    @Produces(MediaType.APPLICATION_XML)
    public Response getDrink(@QueryParam("alcoholic") String alcoholic) {
        Response apiResponse;
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
                    // LA faut mettre une erreur 500
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("<error>Le paramètre 'alcoholic' est invalide ou vide.</error>")
                            .build();
            }
        }

        String fullUrl = API_URL_DRINK + filterPath;
        try {
            apiResponse = client.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>Erreur lors de l'appel à l'API externe: " + e.getMessage() + "</error>")
                    .build();
        }

        String jsonResponse = apiResponse.readEntity(String.class);

        if (alcoholic == null) {
            return Response.ok(convertJsonToXml(jsonResponse)).build();
        }

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject rootJson = jsonReader.readObject();
            JsonArray drinksArray = rootJson.getJsonArray("drinks");

            if (drinksArray == null || drinksArray.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("<error>Aucune boisson trouvée.</error>")
                        .build();
            }

            Random random = new Random();
            int randomIndex = random.nextInt(drinksArray.size());
            JsonObject selectedDrink = drinksArray.getJsonObject(randomIndex);
            String drinkId = selectedDrink.getString("idDrink");

            String detailsUrl = API_URL_DRINK + "lookup.php?i=" + drinkId;
            Response detailsResponse = client.target(detailsUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            return Response.ok(convertJsonToXml(detailsResponse.readEntity(String.class))).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>Erreur lors du traitement des données JSON: " + e.getMessage() + "</error>")
                    .build();
        }
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
        if (drinkJson.containsKey("strTags") && drinkJson.get("strTags") != JsonValue.NULL) {
            syntheticList = Arrays.asList(drinkJson.getString("strTags").split(","));
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


}