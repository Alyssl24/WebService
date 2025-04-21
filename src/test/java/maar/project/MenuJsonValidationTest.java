package maar.project;

import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MenuJsonValidationTest {

    private final String BASE_URL = "http://localhost:8000/v2/recipe/menu";
    private final String SCHEMA_PATH = "src/main/resources/Menu.json";

    private final String VALID_JSON = "{\n" +
            "  \"cuisineType\": \"japanese\",\n" +
            "  \"alcohol\": true,\n" +
            "  \"requiredIngredient\": \"ginger\",\n" +
            "  \"maxPreparationTime\": 30,\n" +
            "  \"constraints\": [\"vegan\", \"gluten-free\"]\n" +
            "}";

    private final String INVALID_CONSTRAINT_JSON = "{\n" +
            "  \"cuisineType\": \"french\",\n" +
            "  \"constraints\": [\"fish-free\"]\n" +
            "}";

    private final String EXTRA_FIELD_JSON = "{\n" +
            "  \"cuisineType\": \"italian\",\n" +
            "  \"bonusField\": \"unexpected\"\n" +
            "}";

    private final String MISSING_REQUIRED_JSON = "{\n" +
            "  \"alcohol\": false\n" +
            "}";

    // ------------------ TESTS ------------------

    @Test
    public void testRequeteMenuValide() throws Exception {
        assertJsonIsValid(VALID_JSON);
        assertResponseIsOk(VALID_JSON);
    }

    @Test
    public void testRequeteAvecContrainteInvalide() throws Exception {
        assertJsonIsInvalid(INVALID_CONSTRAINT_JSON);
    }

    @Test
    public void testRequeteAvecChampEnTrop() throws Exception {
        assertJsonIsInvalid(EXTRA_FIELD_JSON);
    }

    @Test
    public void testRequeteSansChampObligatoire() throws Exception {
        assertJsonIsInvalid(MISSING_REQUIRED_JSON);
    }

    // ------------------ MÉTHODES OUTILS ------------------

    private void assertJsonIsValid(String json) throws Exception {
        JsonValidationService service = JsonValidationService.newInstance();
        try (java.io.Reader schemaReader = Files.newBufferedReader(Paths.get(SCHEMA_PATH))) {
            JsonSchema schema = service.createSchemaReader(schemaReader).read();
            try (JsonReader reader = service.createReader(new StringReader(json), schema, ProblemHandler.throwing())) {
                JsonStructure validated = reader.read();
                assertNotNull(validated, "Le JSON devrait être valide mais ne l’est pas.");
            }
        }
    }

    private void assertJsonIsInvalid(String json) throws Exception {
        JsonValidationService service = JsonValidationService.newInstance();
        try (java.io.Reader schemaReader = Files.newBufferedReader(Paths.get(SCHEMA_PATH))) {
            JsonSchema schema = service.createSchemaReader(schemaReader).read();

            try (JsonReader reader = service.createReader(new StringReader(json), schema, ProblemHandler.throwing())) {
                reader.read();
                fail("Le JSON aurait dû être invalide, mais il est passé.");
            } catch (Exception e) {
                // OK : attendu
            }
        }
    }

    private void assertResponseIsOk(String json) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(json));

        assertEquals(200, response.getStatus(), "Le service n’a pas renvoyé 200 OK.");
        String body = response.readEntity(String.class);
        assertTrue(body.contains("MenuRequest reçu"), "La réponse ne contient pas le message attendu.");
    }
}
