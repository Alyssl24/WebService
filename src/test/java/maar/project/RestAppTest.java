package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RestAppTest {

    private final String BASE_URL = "http://localhost:8000/recipe/meal/";

    /**
     * Cas 1 : Paramètre cuisineType valide ("italian").
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetRecipeSuccess() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian"; // valeur valide
        String url = BASE_URL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        response.bufferEntity(); //pour relire le truc sinon ca bloque la
        Assertions.assertEquals(200, response.getStatus(), "Le code HTTP doit être 200 pour un paramètre valide.");

        String xmlOutput = response.readEntity(String.class);
        Assertions.assertNotNull(xmlOutput, "La réponse XML ne doit pas être nulle.");
        System.out.println("Test Success - XML output :\n" + xmlOutput);
    }


    /**
     * Cas 2 : Paramètre cuisineType invalide (vide).
     * On s'attend à obtenir un code HTTP 400 avec un message d'erreur.
     */
    @Test
    public void testGetRecipeInvalidCuisine() {
        Client client = ClientBuilder.newClient();
        String cuisineType = ""; // valeur invalide
        String url = BASE_URL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        // On attend une réponse 400 (Bad Request)
        Assertions.assertEquals(400, response.getStatus(), "Le code HTTP doit être 400 pour un paramètre cuisineType vide.");

        String errorMessage = response.readEntity(String.class);
        Assertions.assertTrue(errorMessage.contains("invalide"), "Le message d'erreur doit indiquer que le paramètre est invalide.");
        System.out.println("Test Invalid Cuisine - Message d'erreur : " + errorMessage);
    }

    /**
     * Cas 3 : Simulation d'une erreur de l'API externe.
     * Par exemple, en passant une valeur de cuisineType qui n'est pas reconnue par l'API externe.
     * On s'attend à obtenir un code HTTP 500 avec le détail de l'erreur.
     *
     * Remarque : Ce test dépend du comportement de l'API externe et peut être amélioré en utilisant un mock.
     */
    @Test
    public void testGetRecipeApiError() {
        Client client = ClientBuilder.newClient();
        // Utiliser une valeur de cuisineType qui provoque une erreur (par exemple "invalidCuisine")
        String cuisineType = "invalidCuisine";
        String url = BASE_URL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        // Selon l'implémentation, une erreur externe renvoie HTTP 500
        Assertions.assertEquals(500, response.getStatus(), "Le code HTTP doit être 500 en cas d'erreur de l'API externe.");

        String errorMessage = response.readEntity(String.class);
        Assertions.assertTrue(errorMessage.contains("Erreur API externe") ||
                        errorMessage.contains("Erreur lors de l'appel"),
                "Le message d'erreur doit indiquer une erreur lors de l'appel à l'API externe.");
        System.out.println("Test API Error - Message d'erreur : " + errorMessage);
    }
}
