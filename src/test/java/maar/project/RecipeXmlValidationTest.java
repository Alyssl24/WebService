package maar.project;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;

public class RecipeXmlValidationTest {

    private final String BASE_URL = "http://localhost:8000/recipe/meal/";
    private final String XSD_PATH = "src/main/resources/RecipeMeal.xsd"; // Chemin vers votre fichier XSD

    @Test
    public void testXmlConformeAuXsd() throws Exception {
        // Appel de l'API pour obtenir la réponse XML
        Client client = ClientBuilder.newClient();
        String cuisineType = "italian";
        String url = BASE_URL + cuisineType;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        // Vérification du code de retour
        Assertions.assertEquals(200, response.getStatus());

        String xmlOutput = response.readEntity(String.class);
        Assertions.assertNotNull(xmlOutput);
        System.out.println("XML généré :\n" + xmlOutput);

        // Chargement du schéma XSD
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new File(XSD_PATH));
        Validator validator = schema.newValidator();

        // Validation du XML
        try {
            validator.validate(new StreamSource(new StringReader(xmlOutput)));
        } catch (Exception e) {
            // Si une exception est levée, le XML n'est pas conforme au XSD
            Assertions.fail("Le XML généré n'est pas conforme au XSD : " + e.getMessage());
        }
    }
}
