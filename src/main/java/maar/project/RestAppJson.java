package maar.project;

import jakarta.json.*;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import maar.project.meal.json.DetailedIngredient;
import maar.project.meal.json.RecipeResponse;

import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/v2/recipe")
public class RestAppJson extends ApiConfig {
    private final Client client = ClientBuilder.newClient();
    private static final Random RANDOM = new Random();

    @GET
    @Path("/meal/{cuisineType}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipe(@PathParam("cuisineType") String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new RecipeResponse(false, "Edamam", "400"))
                    .build();
        }

        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;

        try {
            Response apiResponse = client.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            int status = apiResponse.getStatus();
            if (status != 200) {
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity(new RecipeResponse(false, "Edamam", String.valueOf(status)))
                        .build();
            }

            String jsonResponse = apiResponse.readEntity(String.class);
            JsonReader reader = Json.createReader(new StringReader(jsonResponse));
            JsonObject rootJson = reader.readObject();

            JsonArray hits = rootJson.getJsonArray("hits");
            if (hits == null || hits.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new RecipeResponse(false, "Edamam", "404"))
                        .build();
            }

            JsonObject recipeJson = hits.getJsonObject(RANDOM.nextInt(hits.size())).getJsonObject("recipe");

            String name = recipeJson.getString("label", "");
            String type = getFirstString(recipeJson, "mealType");
            String country = getFirstString(recipeJson, "cuisineType");
            String prepTime = "PT30M";
            if (recipeJson.containsKey("totalTime") && recipeJson.get("totalTime").getValueType() == JsonValue.ValueType.NUMBER) {
                prepTime = Duration.ofMinutes(recipeJson.getJsonNumber("totalTime").intValue()).toString();
            }
            String image = recipeJson.getString("image", "");
            String source = recipeJson.getString("url", "");

            JsonArray ingArray = recipeJson.getJsonArray("ingredients");
            List<DetailedIngredient> detailedIngredients = new ArrayList<>();
            List<String> ingredientNoms = new ArrayList<>();
            if (ingArray != null) {
                for (JsonValue v : ingArray) {
                    JsonObject ing = v.asJsonObject();
                    String nom = ing.getString("food", "");
                    String qt = ing.containsKey("quantity") && ing.get("quantity").getValueType() == JsonValue.ValueType.NUMBER
                            ? ing.getJsonNumber("quantity").toString() : "";
                    String img = ing.getString("image", "");
                    detailedIngredients.add(new DetailedIngredient(nom, qt, img));
                    ingredientNoms.add(nom);
                }
            }
            String ingredients = String.join(", ", ingredientNoms);

            // JE NE TROUVE PAS LA DONNE DANS L API DONC J AI MIS DES DONNEES MOQUE EN ATTENDANT
            List<String> instructions = new ArrayList<>();
            instructions.add("Préparer les ingrédients.");
            instructions.add("Cuire selon les instructions.");
            instructions.add("Servir chaud.");

            RecipeResponse recipe = new RecipeResponse(
                    true, name, type, country, prepTime, image, source, ingredients, detailedIngredients, instructions
            );

            return Response.ok(recipe).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new RecipeResponse(false, "Edamam", "500"))
                    .build();
        }
    }

    private String getFirstString(JsonObject obj, String key) {
        if (obj.containsKey(key) && obj.get(key).getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray arr = obj.getJsonArray(key);
            if (!arr.isEmpty()) {
                return arr.getString(0);
            }
        }
        return "";
    }
}
