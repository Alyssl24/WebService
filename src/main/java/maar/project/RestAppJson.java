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
import maar.project.menu.json.MenuRequest;
import maar.project.menu.json.MenuResponse;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/v2/recipe")
@OpenAPIDefinition(
        info = @Info(
                title = "API Recettes MAAR - JSON & XML",
                version = "2.1",
                description = "Services REST pour les plats et boissons"
        ),
        servers = {
                @Server(url = "http://localhost:8000", description = "Serveur local")
        }
)
public class RestAppJson extends ApiConfig {
    private final Client client = ClientBuilder.newClient();
    private static final Random RANDOM = new Random();

    @POST
    @Path("/menu")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Génère un menu complet aléatoire",
            description = "Retourne un menu JSON contenant une entrée, un plat, un dessert et une boisson, tous générés aléatoirement mais en respectant les filtres passés dans la requête. Les données proviennent de l’API Edamam (pour les plats) et TheCocktailDB (pour les boissons).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu généré avec succès"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides ou aucun résultat trouvé"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne lors de la génération du menu")
            }
    )
    public Response getMenu(String rawJson) {
        try {
            JsonValidationService service = JsonValidationService.newInstance();
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("Menu.json");
            if (schemaStream == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur : fichier Menu.json introuvable dans resources/").build();
            }
            JsonSchema schema = service.readSchema(schemaStream);

            JsonReader reader = service.createReader(
                    new StringReader(rawJson),
                    schema,
                    ProblemHandler.throwing()
            );
            JsonObject validated = reader.readObject();

            jakarta.json.bind.Jsonb jsonb = jakarta.json.bind.JsonbBuilder.create();
            MenuRequest request = jsonb.fromJson(rawJson, MenuRequest.class);

            RecipeResponse entree = fetchValidRecipe(request, this);
            RecipeResponse plat = fetchValidRecipe(request, this);
            RecipeResponse dessert = fetchValidRecipe(request, this);
            DrinkResponse boisson = fetchValidDrink(request, this);

            int totalPreparationTime = parseDuration(entree.getPreparationTime()) +
                    parseDuration(plat.getPreparationTime()) +
                    parseDuration(dessert.getPreparationTime());

            if (request.getMaxPreparationTime() != null && totalPreparationTime > request.getMaxPreparationTime()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Temps de préparation total trop élevé pour le max fourni.").build();
            }

            MenuResponse menu = new MenuResponse();
            menu.setEntree(entree);
            menu.setPlat(plat);
            menu.setDessert(dessert);
            menu.setBoisson(boisson);
            menu.setPreparationTime(totalPreparationTime);
            menu.setTotalCalories(calculateTotalCalories(entree, plat, dessert));
            return Response.ok(menu).build();

        } catch (JsonException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Requête d'entrée JSON invalide : " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur serveur : " + e.getMessage()).build();
        }
    }

    private RecipeResponse callEdamamApi(String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            return new RecipeResponse(false, "Edamam", "400");
        }

        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;

        try (Client tempClient = ClientBuilder.newClient()) {
            try (Response apiResponse = tempClient.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get()) {

                int status = apiResponse.getStatus();
                if (status != 200) {
                    return new RecipeResponse(false, "Edamam", String.valueOf(status));
                }

                String jsonResponse = apiResponse.readEntity(String.class);
                JsonReader reader = Json.createReader(new StringReader(jsonResponse));
                JsonObject rootJson = reader.readObject();

                JsonArray hits = rootJson.getJsonArray("hits");
                if (hits == null || hits.isEmpty()) {
                    return new RecipeResponse(false, "Edamam", "404");
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

                // Instructions provisoires
                List<String> instructions = new ArrayList<>();
                instructions.add("Préparer les ingrédients.");
                instructions.add("Cuire selon les instructions.");
                instructions.add("Servir chaud.");

                // Récupérer les calories du recipe
                double calories = 0;
                if (recipeJson.containsKey("calories") && recipeJson.get("calories").getValueType() == JsonValue.ValueType.NUMBER) {
                    calories = recipeJson.getJsonNumber("calories").doubleValue();
                }


                RecipeResponse response = new RecipeResponse(
                        true, name, type, country, prepTime, image, source, ingredients, detailedIngredients, instructions
                );
                response.setCalories(calories);
                return response;
            }
        } catch (Exception e) {
            return new RecipeResponse(false, "Edamam", "500");
        }
    }

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
        RecipeResponse recipe = callEdamamApi(cuisineType);
        return Response.ok(recipe).build();
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
        DrinkResponse drink = callDrinkApi(alcoholic == null ? null : alcoholic.equals("true"), null);
        return Response.ok(drink).build();
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

    private DrinkResponse callDrinkApi(Boolean alcohol, String requiredIngredient) {
        String filterPath;
        if (alcohol == null) {
            filterPath = "random.php";
        } else if (alcohol) {
            filterPath = "filter.php?a=Alcoholic";
        } else {
            filterPath = "filter.php?a=Non_Alcoholic";
        }

        try (Client tempClient = ClientBuilder.newClient()) {
            try (Response apiResponse = tempClient.target(API_URL_DRINK + filterPath)
                    .request(MediaType.APPLICATION_JSON)
                    .get()) {

                TheCocktailDBResponse raw = apiResponse.readEntity(TheCocktailDBResponse.class);

                if (raw.getDrinks() == null || raw.getDrinks().isEmpty()) {
                    DrinkResponse error = new DrinkResponse();
                    error.setSuccess(false);
                    error.setApi_failed("TheCocktailDB");
                    error.setApi_status("404");
                    return error;
                }

                TheCocktailDBResponse.Drink drink;

                if (alcohol == null) {
                    drink = raw.getDrinks().get(0);
                } else {
                    int randIndex = RANDOM.nextInt(raw.getDrinks().size());
                    String idDrink = raw.getDrinks().get(randIndex).getIdDrink();

                    try (Response detailsResponse = tempClient.target(API_URL_DRINK + "lookup.php?i=" + idDrink)
                            .request(MediaType.APPLICATION_JSON)
                            .get()) {

                        TheCocktailDBResponse details = detailsResponse.readEntity(TheCocktailDBResponse.class);
                        drink = details.getDrinks().get(0);
                    }
                }

                DrinkResponse formatted = convertToDrinkResponse(drink);

                if (requiredIngredient != null) {
                    boolean found = formatted.getIngredients().stream()
                            .anyMatch(ingredient -> ingredient.equalsIgnoreCase(requiredIngredient));
                    if (!found) {
                        DrinkResponse error = new DrinkResponse();
                        error.setSuccess(false);
                        error.setApi_failed("TheCocktailDB");
                        error.setApi_status("404");
                        return error;
                    }
                }

                return formatted;
            }
        } catch (Exception e) {
            DrinkResponse error = new DrinkResponse();
            error.setSuccess(false);
            error.setApi_failed("TheCocktailDB");
            error.setApi_status("500");
            return error;
        }
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

    public static RecipeResponse fetchValidRecipe(MenuRequest request, RestAppJson api) {
        while (true) {
            RecipeResponse recipe = api.callEdamamApi(request.getCuisineType());

            if (!recipe.isSuccess()) continue;

            if (request.getConstraints() != null && !request.getConstraints().isEmpty()) {
                boolean respectsConstraints = request.getConstraints().stream()
                        .allMatch(constraint -> recipeMatchesConstraint(recipe, constraint));

                if (!respectsConstraints) continue;
            }

            return recipe;
        }
    }

    public static DrinkResponse fetchValidDrink(MenuRequest request, RestAppJson api) {
        while (true) {
            Boolean alcoholic = request.getAlcohol();
            DrinkResponse drink = api.callDrinkApi(alcoholic, request.getRequiredIngredient());

            if (!drink.isSuccess()) continue;

            if (request.getRequiredIngredient() != null) {
                boolean found = drink.getIngredients().stream()
                        .anyMatch(ingredient -> ingredient.equalsIgnoreCase(request.getRequiredIngredient()));

                if (!found) continue;
            }

            return drink;
        }
    }

    public static int parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) return 0;
        return (int) Duration.parse(duration).toMinutes();
    }

    public static boolean recipeMatchesConstraint(RecipeResponse recipe, String constraint) {
        String ingredients = recipe.getIngredients().toLowerCase();

        switch (constraint) {
            case "vegetarian":
            case "vegan":
                return !ingredients.contains("meat") && !ingredients.contains("chicken") && !ingredients.contains("fish");
            case "gluten-free":
                return !ingredients.contains("wheat") && !ingredients.contains("bread");
            case "pork-free":
                return !ingredients.contains("pork") && !ingredients.contains("bacon");
            case "dairy-free":
                return !ingredients.contains("cheese") && !ingredients.contains("milk");
            default:
                return true;
        }
    }

    public static int calculateTotalCalories(RecipeResponse entree, RecipeResponse plat, RecipeResponse dessert) {
        return (int)(entree.getCalories() + plat.getCalories() + dessert.getCalories());
    }

}