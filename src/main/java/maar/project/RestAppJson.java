package maar.project;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.json.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import maar.project.drinks.json.DrinkResponse;
import maar.project.drinks.json.TheCocktailDBResponse;
import maar.project.meal.json.DetailedIngredient;
import maar.project.meal.json.RecipeResponse;

import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/v2/recipe")
@OpenAPIDefinition(
        info = @Info(
                title = "API Recettes MAAR - JSON",
                version = "2.1",
                description = "Services REST JSON pour les plats et boissons"
        ),
        servers = {
                @Server(url = "http://localhost:8000", description = "Serveur local")
        }
)
public class RestAppJson extends ApiConfig {
    private final Client client = ClientBuilder.newClient();
    private static final Random RANDOM = new Random();

    @GET
    @Path("/meal/{cuisineType: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get a random recipe by cuisine type",
            description = "Returns a JSON-formatted recipe that conforms to a JSON Schema. Uses the Edamam API.",
            parameters = {
                    @Parameter(name = "cuisineType", description = "Cuisine type (e.g., italian, french, chinese)", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe found"),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid parameter"),
                    @ApiResponse(responseCode = "404", description = "No recipe found"),
                    @ApiResponse(responseCode = "502", description = "Communication error with Edamam API"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
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

    @GET
    @Path("/drink")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get a random drink",
            description = "Returns a drink depending on the 'alcoholic' parameter (true, false or null). Uses TheCocktailDB.",
            parameters = {
                    @Parameter(name = "alcoholic", description = "true = alcoholic, false = non-alcoholic, null = random")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Drink found"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameter"),
                    @ApiResponse(responseCode = "404", description = "No drink found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Response getDrinkJson(@QueryParam("alcoholic") String alcoholic) {
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
                    return buildErrorResponse("400");
            }
        }

        try {
            Response rawResponse = client.target(API_URL_DRINK + filterPath)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            TheCocktailDBResponse raw = rawResponse.readEntity(TheCocktailDBResponse.class);
            if (raw.getDrinks() == null || raw.getDrinks().isEmpty()) {
                return buildErrorResponse("404");
            }

            TheCocktailDBResponse.Drink drink;
            if (alcoholic == null) {
                drink = raw.getDrinks().get(0);
            } else {
                int randIndex = RANDOM.nextInt(raw.getDrinks().size());
                String idDrink = raw.getDrinks().get(randIndex).getIdDrink();

                Response detailsResponse = client.target(API_URL_DRINK + "lookup.php?i=" + idDrink)
                        .request(MediaType.APPLICATION_JSON)
                        .get();

                TheCocktailDBResponse details = detailsResponse.readEntity(TheCocktailDBResponse.class);
                drink = details.getDrinks().get(0);
            }

            DrinkResponse formatted = convertToDrinkResponse(drink);
            return Response.ok(formatted).build();

        } catch (Exception e) {
            return buildErrorResponse("500");
        }
    }

    private Response buildErrorResponse(String code) {
        DrinkResponse error = new DrinkResponse();
        error.setSuccess(false);
        error.setApi_failed("TheCocktailDB");
        error.setApi_status(code);
        return Response.status(Integer.parseInt(code)).entity(error).build();
    }

    public DrinkResponse convertToDrinkResponse(TheCocktailDBResponse.Drink rawDrink) {
        DrinkResponse result = new DrinkResponse();
        result.setSuccess(true);
        result.setName(rawDrink.getStrDrink());
        result.setType(rawDrink.getStrCategory());
        result.setAlcoholic("Alcoholic".equals(rawDrink.getStrAlcoholic()));
        result.setImageURL(rawDrink.getStrDrinkThumb());
        result.setInstructions(rawDrink.getStrInstructions());

        List<String> ingredients = new ArrayList<>();
        List<DrinkResponse.DetailedIngredient> detailed = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            try {
                String ingredient = (String) rawDrink.getClass().getMethod("getStrIngredient" + i).invoke(rawDrink);
                String measure = (String) rawDrink.getClass().getMethod("getStrMeasure" + i).invoke(rawDrink);

                if (ingredient != null) {
                    ingredients.add(ingredient);

                    DrinkResponse.DetailedIngredient d = new DrinkResponse.DetailedIngredient();
                    d.setName(ingredient);
                    d.setQuantity(measure != null ? measure : "");
                    d.setImage(""); // l'API ne donne pas d’image
                    detailed.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace(); // facultatif
            }
        }

        result.setIngredients(ingredients);
        result.setDetailedIngredients(detailed);

        return result;
    }

}
