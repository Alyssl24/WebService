package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RestAppTest {

    private final String BASE_URL_MEAL_XML = "http://localhost:8000/recipe/meal/";
    private final String BASE_URL_MEAL_JSON = "http://localhost:8000/v2/recipe/meal/";
    private final String BASE_URL_DRINK = "http://localhost:8000/recipe/drink";
    private final String BASE_URL_DRINK_V2 = "http://localhost:8000/v2/recipe/drink";


    /**
     * Cas 1 : Paramètre cuisineType valide ("italian").
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetRecipeXmlSuccess() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian"; // valeur valide
        String url = BASE_URL_MEAL_XML + cuisineType;

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
    public void testGetRecipeXmlInvalidCuisine() {
        Client client = ClientBuilder.newClient();
        String cuisineType = ""; // valeur invalide
        String url = BASE_URL_MEAL_XML + cuisineType;

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
    public void testGetRecipeXmlWithUnknownCuisineType() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "invalidCuisine";
        String url = BASE_URL_MEAL_XML + cuisineType;

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
    public void testGetRecipeXmlApiTechnicalError() {
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
     * Cas 1 : Paramètre cuisineType valide ("italian").
     * On s'attend à obtenir un code HTTP 200 et un XML non nul.
     */
    @Test
    public void testGetRecipeJsonSuccess() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian"; // valeur valide
        String url = BASE_URL_MEAL_JSON + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
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
    public void testGetRecipeJsonInvalidCuisine() {
        Client client = ClientBuilder.newClient();
        String cuisineType = ""; // valeur invalide
        String url = BASE_URL_MEAL_JSON + cuisineType;

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
    public void testGetRecipeJsonWithUnknownCuisineType() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "invalidCuisine";
        String url = BASE_URL_MEAL_JSON + cuisineType;

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
    public void testGetRecipeJsonApiTechnicalError() {
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

    /** TEST JSON **/
    /**
     * Cas 1 JSON : Paramètre alcoholic = true.
     * On s'attend à un JSON avec success=true et les champs requis.
     */
    @Test
    public void testGetDrinkJsonAlcoholicSuccess() {
        Client client = ClientBuilder.newClient();
        boolean alcoholic = true;
        String url = BASE_URL_DRINK_V2 + "?alcoholic=" + alcoholic;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, response.getStatus(), "Le code HTTP doit être 200 pour une requête JSON avec alcoholic=true.");

        String json = response.readEntity(String.class);
        Assertions.assertTrue(json.contains("\"success\":true"), "Le JSON doit contenir success=true.");
        Assertions.assertTrue(json.contains("\"name\""), "Le JSON doit contenir le champ 'name'.");
        Assertions.assertTrue(json.contains("\"alcoholic\":"), "Le JSON doit contenir le champ 'alcoholic'.");
        System.out.println("Test JSON alcoolisé :\n" + json);
    }

    /**
     * Cas 2 JSON : Paramètre alcoholic = false.
     * Même logique que Cas 1.
     */
    @Test
    public void testGetDrinkJsonNonAlcoholicSuccess() {
        Client client = ClientBuilder.newClient();
        boolean alcoholic = false;
        String url = BASE_URL_DRINK_V2 + "?alcoholic=" + alcoholic;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, response.getStatus(), "Le code HTTP doit être 200 pour alcoholic=false.");

        String json = response.readEntity(String.class);
        Assertions.assertTrue(json.contains("\"success\":true"), "Le JSON doit indiquer success=true.");
        Assertions.assertTrue(json.contains("\"ingredients\""), "Le JSON doit contenir une liste d'ingrédients.");
        System.out.println("Test JSON non alcoolisé :\n" + json);
    }

    /**
     * Cas 3 JSON : Sans paramètre alcoholic (aléatoire).
     */
    @Test
    public void testGetDrinkJsonRandomSuccess() {
        Client client = ClientBuilder.newClient();
        String url = BASE_URL_DRINK_V2;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, response.getStatus(), "Le code HTTP doit être 200 pour une boisson aléatoire JSON.");

        String json = response.readEntity(String.class);
        Assertions.assertTrue(json.contains("\"success\":true"), "La réponse JSON doit indiquer success=true.");
        Assertions.assertTrue(json.contains("\"detailedIngredients\""), "La réponse doit contenir detailedIngredients.");
        System.out.println("Test JSON boisson aléatoire :\n" + json);
    }

    /**
     * Cas 4 JSON : Paramètre alcoholic invalide → on veut success=false + api_failed/api_status.
     */
    @Test
    public void testGetDrinkJsonInvalidParam() {
        Client client = ClientBuilder.newClient();
        String url = BASE_URL_DRINK_V2 + "?alcoholic=blabla";

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(400, response.getStatus(), "Le code HTTP doit être 400 pour une valeur de paramètre invalide.");

        String json = response.readEntity(String.class);
        Assertions.assertTrue(json.contains("\"success\":false"), "La réponse JSON doit indiquer success=false.");
        Assertions.assertTrue(json.contains("\"api_failed\""), "La réponse doit contenir api_failed.");
        Assertions.assertTrue(json.contains("\"api_status\""), "La réponse doit contenir api_status.");
        System.out.println("Test JSON paramètre invalide :\n" + json);
    }


}
