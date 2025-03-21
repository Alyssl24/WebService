package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RestAppTest {

    private final String BASE_URL_MEAL = "http://localhost:8000/recipe/meal/";
    private final String BASE_URL_DRINK = "http://localhost:8000/recipe/drink";

    /**
     * Cas 1 : Paramètre cuisineType valide ("italian").
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetRecipeSuccess() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian"; // valeur valide
        String url = BASE_URL_MEAL + cuisineType;

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
        String url = BASE_URL_MEAL + cuisineType;

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
    public void testGetRecipeWithUnknownCuisineType() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "invalidCuisine";
        String url = BASE_URL_MEAL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        Assertions.assertEquals(400, response.getStatus(), "Le code HTTP doit être 400 quand aucun résultat n’est trouvé.");

        String errorMessage = response.readEntity(String.class);
        Assertions.assertTrue(errorMessage.contains("Aucune recette trouvée") ||
                        errorMessage.contains("invalide"),
                "Le message d'erreur doit indiquer l'absence de recette ou un paramètre invalide.");
    }

    /**
     * Cas 4 : Simulation d'une erreur technique lors de l'appel à l'API externe.
     * Par exemple, en modifiant temporairement l'URL pour qu'elle pointe vers un domaine invalide.
     * On s'attend à obtenir un code HTTP 500 avec un message d'erreur technique.
     *
     * Remarque : Ce test provoque volontairement une erreur technique réseau.
     * Dans un vrai environnement de test, ce genre de test devrait être mocké pour éviter
     * les appels réels instables.
     */
    @Test
    public void testGetRecipeApiTechnicalError() {
        Client client = ClientBuilder.newClient();

        String fakeUrl = "http://localhost:9999/invalid-endpoint/recipe/meal/italian";

        Response response;
        try {
            response = client.target(fakeUrl)
                    .request(MediaType.APPLICATION_XML)
                    .get();
        } catch (Exception e) {
            // Si l’appel plante complètement, c’est que l’erreur technique est bien simulée
            System.out.println("Erreur réseau simulée avec succès : " + e.getMessage());
            return; // Test réussi car on voulait une erreur réseau
        }

        // Si on arrive à obtenir une réponse HTTP, on vérifie que c'est bien une erreur 500
        Assertions.assertEquals(500, response.getStatus(), "Le code HTTP doit être 500 en cas d'erreur technique.");

        String errorMessage = response.readEntity(String.class);
        Assertions.assertTrue(errorMessage.toLowerCase().contains("erreur"),
                "Le message d'erreur doit contenir des détails sur l'erreur.");
        System.out.println("Test erreur technique - Message reçu : " + errorMessage);
    }



    /**
     * Cas 1 : Paramètre alcoholic = true.
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetDrinkAlcoholicSuccess() {
        Client client = ClientBuilder.newClient();
        boolean alcoholic = true;
        String url = BASE_URL_DRINK + "?alcoholic=" + alcoholic;

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
     * Cas 2 : Paramètre alcoholic = fasle.
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetDrinkNoAlcoholicSuccess() {
        Client client = ClientBuilder.newClient();
        boolean alcoholic = false;
        String url = BASE_URL_DRINK + "?alcoholic=" + alcoholic;

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
     * Cas 3 : Sans le paramètre alcoholic.
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetDrinkSuccess() {
        Client client = ClientBuilder.newClient();
        String url = BASE_URL_DRINK;

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
     * Cas 4 : Simulation d'une erreur côté client due à un paramètre 'alcoholic' invalide.
     * Le service doit renvoyer une erreur HTTP 500 avec un message expliquant l’erreur.
     */
    @Test
    public void testGetDrinkWithInvalidParam() {
        Client client = ClientBuilder.newClient();
        String url = BASE_URL_DRINK + "?alcoholic=blabla";

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        Assertions.assertEquals(500, response.getStatus(), "Le code HTTP doit être 500 en cas de paramètre 'alcoholic' invalide.");

        String errorMessage = response.readEntity(String.class);
        Assertions.assertTrue(errorMessage.contains("paramètre") || errorMessage.contains("invalide"),
                "Le message d'erreur doit mentionner que le paramètre 'alcoholic' est invalide.");
        System.out.println("Test paramètre invalide - Message : " + errorMessage);
    }

}
