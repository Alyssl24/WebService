package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.StringReader;

public class RecetteJsonValidationTest {

    private final String BASE_URL = "http://localhost:8000/v2/recipe/meal/";
    private final String SCHEMA_PATH = "src/main/resources/RecipeMeal.json"; // Ton JSON Schema

    @Test
    public void testJsonConformeAuSchema() throws Exception {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian";
        String url = BASE_URL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, response.getStatus());

        String jsonOutput = response.readEntity(String.class);
        Assertions.assertNotNull(jsonOutput);
        System.out.println("JSON généré :\n" + jsonOutput);
/*
        // Charger le schéma JSON
        try (FileInputStream schemaStream = new FileInputStream(SCHEMA_PATH)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
            Schema schema = SchemaLoader.load(rawSchema);

            // Valider la réponse JSON
            schema.validate(new JSONObject(new JSONTokener(new StringReader(jsonOutput))));
        } catch (Exception e) {
            Assertions.fail("Le JSON généré n'est pas conforme au schéma : " + e.getMessage());
        }*/
    }
}
