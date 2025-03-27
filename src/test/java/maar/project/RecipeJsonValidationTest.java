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

public class RecipeJsonValidationTest {

    private final String BASE_URL = "http://localhost:8000/v2/recipe/meal/";
    private final String SCHEMA_PATH = "src/main/resources/RecipeMeal.json";

    @Test
    public void testJsonRespecteLeSchema() throws Exception {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian";
        Response response = client.target(BASE_URL + cuisineType)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(200, response.getStatus());

        String jsonOutput = response.readEntity(String.class);
        assertNotNull(jsonOutput);

        JsonValidationService service = JsonValidationService.newInstance();

        JsonSchema schema;
        try (java.io.Reader reader = Files.newBufferedReader(Paths.get(SCHEMA_PATH))) {
            schema = service.createSchemaReader(reader).read();
        }

        ProblemHandler handler = service.createProblemPrinter(System.out::println);
        try (JsonReader reader = service.createReader(new StringReader(jsonOutput), schema, handler)) {
            JsonStructure validated = reader.read();
            assertNotNull(validated);
        }
    }
}
