package maar.project;

import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
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

public class DrinkJsonValidationTest {

    private static final String ENDPOINT_URL = "http://localhost:8000/v2/recipe/drink";
    private static final String SCHEMA_PATH = "src/main/resources/RecipeDrink.json";

    @Test
    public void testDrinkJsonStructureIsValid() throws Exception {
        Client client = ClientBuilder.newClient();

        Response response = client.target(ENDPOINT_URL)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus(), "La réponse HTTP doit être 200");

        String json = response.readEntity(String.class);
        assertNotNull(json, "La réponse JSON ne doit pas être nulle");

        JsonValidationService service = JsonValidationService.newInstance();

        JsonSchema schema;
        try (java.io.Reader reader = Files.newBufferedReader(Paths.get(SCHEMA_PATH))) {
            schema = service.createSchemaReader(reader).read();
        }

        ProblemHandler handler = service.createProblemPrinter(System.out::println);

        try (JsonReader jsonReader = service.createReader(new StringReader(json), schema, handler)) {
            JsonStructure jsonStructure = jsonReader.read();
            assertNotNull(jsonStructure, "La structure JSON ne doit pas être nulle");
        }
    }
}
