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
import maar.project.recette.Ingredient;
import maar.project.recette.Recette;
import maar.project.recette.TypeDetails;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/recipe")
public class RestApp {

    private static final String API_URL = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "5b00ff05";
    private static final String APP_KEY = "d3807873187ac6b6d68f52a28f39a00e";

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

            System.out.println("ICI ON AFFICHE LE RECIPE JSON =>");
            System.out.println(recipeJson);

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
}
