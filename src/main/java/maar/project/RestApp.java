package maar.project;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import maar.project.recette.Recette;


@Path("/recipe")
public class RestApp {
    private static final String API_URL = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "5b00ff05";
    private static final String APP_KEY = "d3807873187ac6b6d68f52a28f39a00e";

    private final Client client = ClientBuilder.newClient();

    public static void main( String[] args )
    {
        RestApp app = new RestApp();
        System.out.println(app.getRecipe("Indian"));
    }

    public String getToken_jaxb(String cuisineType) {
        return getRecipe(cuisineType).readEntity(Recette.class).toString();
    }

    @GET
    @Path("/meal/{cuisineType}")
    public Response getRecipe(@PathParam("cuisineType") String cuisineType) {
        String fullUrl = API_URL + "?type=public&app_id=" + APP_ID + "&app_key=" + APP_KEY + "&cuisineType=" + cuisineType;

        Response apiResponse = client.target(fullUrl)
                .request(MediaType.APPLICATION_JSON)
                .get();
        return apiResponse;
    }

}
