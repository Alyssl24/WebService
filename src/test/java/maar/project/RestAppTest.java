package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RestAppTest {

    @Test
    public void testGetRecipe() {
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian"; // ou une autre valeur valide
        String url = "http://localhost:8000/recipe/meal/" + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        // Vérifier que la réponse HTTP est 200
        Assertions.assertEquals(200, response.getStatus());

        // Optionnel : récupérer le XML et le vérifier
        String xmlOutput = response.readEntity(String.class);
        Assertions.assertNotNull(xmlOutput);
        System.out.println(xmlOutput);
    }
}
