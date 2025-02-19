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
import maar.project.recette.Recette;
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
                    .getJsonObject(0)  // c ici qu'on récupère juste le premier elt de hits
                    .getJsonObject("recipe");

            JsonArray ingredientsArray = recipeJson.getJsonArray("ingredientLines");
            List<String> ingredientLines = new ArrayList<>();
            if (ingredientsArray != null) {
                for (int i = 0; i < ingredientsArray.size(); i++) {
                    ingredientLines.add(ingredientsArray.getString(i));
                }
            }

            JsonArray TypeCuisineArray = recipeJson.getJsonArray("cuisineType");
            List<String> TypeCuisineLines = new ArrayList<>();
            if (TypeCuisineArray != null) {
                for (int i = 0; i < TypeCuisineArray.size(); i++) {
                    TypeCuisineLines.add(TypeCuisineArray.getString(i));
                }
            }

            JsonArray TypeRepasArray = recipeJson.getJsonArray("mealType");
            List<String> TypeRepasLines = new ArrayList<>();
            if (TypeRepasArray != null) {
                for (int i = 0; i < TypeRepasArray.size(); i++) {
                    TypeRepasLines.add(TypeRepasArray.getString(i));
                }
            }

            JsonArray TypePlatsArray = recipeJson.getJsonArray("dishType");
            List<String> TypePlatsLines = new ArrayList<>();
            if (TypePlatsArray != null) {
                for (int i = 0; i < TypePlatsArray.size(); i++) {
                    TypePlatsLines.add(TypePlatsArray.getString(i));
                }
            }

            double totalTime = recipeJson.containsKey("totalTime") ?
                    recipeJson.getJsonNumber("totalTime").doubleValue() : 30.0;
            int totalMinutes = (int) totalTime;
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            LocalTime localTime = LocalTime.of(hours, minutes, 0);
            String timeFormatted = localTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

            recette = new Recette(
                    recipeJson.getString("uri"),
                    recipeJson.getString("label"),
                    TypeRepasLines,
                    TypePlatsLines,
                    TypeCuisineLines,
                    timeFormatted,
                    recipeJson.getString("image"),
                    recipeJson.getString("url"),
                    BigDecimal.valueOf(recipeJson.getJsonNumber("calories").doubleValue()),
                    // il faudrait recuperer tout ca par rapport au tableau "ingredients" dans le json
                    // le text complet est dans "ingredientsLines"
                    // mais j'avoue que je n'y arrive pas donc si jamais tu peux voir pour le faire
                    // attention ya des modif a faire dans le xsd (surtout quand l'elt en json est une list)
                    // ingredientsLines est un peut fait n'importe comment car il prends les elts de "ingredientLines"
                    "Exemple de texte complet de l'ingrédient",
                    "NomPur Exemple",
                    "1 portion",
                    "https://example.com/ingredient.jpg",
                    "Lait",
                    ingredientLines
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
        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;

        String jsonResponse = client.target(fullUrl)
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        Recette recette = convertJsonToRecette(jsonResponse);

        StringWriter xmlWriter = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Recette.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(recette, xmlWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok(xmlWriter.toString()).build();
    }
}
