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

            recette = new Recette(
                    recipeJson.getString("uri"),
                    recipeJson.getString("label"),
                    "dejeuner",
                    "plat",
                    "Indian",
                    "00:30:00",
                    recipeJson.getString("image"),
                    recipeJson.getString("url"),
                    BigDecimal.valueOf(recipeJson.getJsonNumber("calories").doubleValue()),
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
