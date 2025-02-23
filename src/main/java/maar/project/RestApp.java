package maar.project;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
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

@Path("/recipe")
public class RestApp {
    private static final String API_URL = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "5b00ff05";
    private static final String APP_KEY = "d3807873187ac6b6d68f52a28f39a00e";

    private final Client client = ClientBuilder.newClient();

    public static Recette convertJsonToRecette(String jsonResponse) {
        Recette recette = null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse))) {
            JsonObject rootJson = jsonReader.readObject();

            JsonObject recipeJson = rootJson.getJsonArray("hits")
                    .getJsonObject(0)  // système de mémoïsation : on récupère le premier élément
                    .getJsonObject("recipe");

            System.out.println("ICI ON AFFICHE LE RECIPE JSON =>");
            System.out.println(recipeJson);

            // Récupération du tableau "ingredients" du JSON
            JsonArray ingredientsDetailArray = recipeJson.getJsonArray("ingredients");
            List<Ingredient> ingredients = new ArrayList<>();
            if (ingredientsDetailArray != null) {
                for (int i = 0; i < ingredientsDetailArray.size(); i++) {
                    JsonObject ingredientJson = ingredientsDetailArray.getJsonObject(i);

                    // "text" correspond ici au texte complet de l'ingrédient
                    String texteComplet = ingredientJson.getString("text", "");
                    // "food" peut servir de nom pur
                    String nomPur = ingredientJson.getString("food", "");
                    // Pour la quantité, on peut récupérer "quantity" et éventuellement "measure"
                    String quantite = ingredientJson.containsKey("quantity")
                            ? ingredientJson.getJsonNumber("quantity").toString()
                            + (ingredientJson.containsKey("measure") ? " " + ingredientJson.getString("measure") : "")
                            : "";
                    // "image" si disponible
                    String imageIngredient = ingredientJson.getString("image", "");

                    Ingredient ing = new Ingredient(texteComplet, nomPur, quantite, imageIngredient);
                    ingredients.add(ing);
                }
            }

            // Récupération des types depuis les tableaux JSON
            JsonArray typeCuisineArray = recipeJson.getJsonArray("cuisineType");
            List<String> typesCuisine = new ArrayList<>();
            if (typeCuisineArray != null) {
                for (int i = 0; i < typeCuisineArray.size(); i++) {
                    typesCuisine.add(typeCuisineArray.getString(i));
                }
            }

            JsonArray typeRepasArray = recipeJson.getJsonArray("mealType");
            List<String> typesRepas = new ArrayList<>();
            if (typeRepasArray != null) {
                for (int i = 0; i < typeRepasArray.size(); i++) {
                    String value = typeRepasArray.getString(i);
                    if (value.contains("/")) {
                        // On sépare la chaîne par le caractère '/'
                        String[] parts = value.split("/");
                        for (String part : parts) {
                            typesRepas.add(part.trim());
                        }
                    } else {
                        typesRepas.add(value);
                    }
                }
            }

            JsonArray typePlatArray = recipeJson.getJsonArray("dishType");
            List<String> typesPlat = new ArrayList<>();
            if (typePlatArray != null) {
                for (int i = 0; i < typePlatArray.size(); i++) {
                    typesPlat.add(typePlatArray.getString(i));
                }
            }

            // Conversion du temps total (en minutes) en format heure
            double totalTime = recipeJson.containsKey("totalTime")
                    ? recipeJson.getJsonNumber("totalTime").doubleValue()
                    : 30.0;
            int totalMinutes = (int) totalTime;
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            LocalTime localTime = LocalTime.of(hours, minutes, 0);
            String timeFormatted = localTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

            // Création du TypeDetails pour regrouper les types de repas, plat et cuisine
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
                    null,
                    BigDecimal.valueOf(recipeJson.getJsonNumber("calories").doubleValue())
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

        // valeur de cuisineType non reconnu
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Le paramètre 'cuisineType' est invalide ou vide.")
                    .build();
        }

        //On recupere la reponse par une requette HTTP.
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

        // Si l'API externe retourne un code autre que 200 on renvoie une erreur 500
        if (apiResponse.getStatus() != 200) {
            String errorMessage = apiResponse.readEntity(String.class);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur API externe (HTTP " + apiResponse.getStatus() + "): " + errorMessage)
                    .build();
        }

        String jsonResponse = apiResponse.readEntity(String.class);

        //On convertit la reponse en un objet Recette.
        Recette recette = convertJsonToRecette(jsonResponse);

        //On convertit ensuite l'objet en un fichier XML.
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
