package maar.project.meal.json;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;

import java.util.List;

@JsonbPropertyOrder({
        "success",
        "name",
        "type",
        "country",
        "preparationTime",
        "imageURL",
        "source",
        "ingredients",
        "detailedIngredients",
        "instructions",
        "api_failed",
        "api_status"
})
public class RecipeResponse {

    @JsonbProperty("success")
    private boolean success;

    @JsonbProperty("name")
    private String name;

    @JsonbProperty("type")
    private String type;

    @JsonbProperty("country")
    private String country;

    @JsonbProperty("preparationTime")
    private String preparationTime;

    @JsonbProperty("imageURL")
    private String imageURL;

    @JsonbProperty("source")
    private String source;

    @JsonbProperty("ingredients")
    private String ingredients;

    @JsonbProperty("detailedIngredients")
    private List<DetailedIngredient> detailedIngredients;

    @JsonbProperty("instructions")
    private List<String> instructions;

    @JsonbProperty("api_failed")
    private String apiFailed;

    @JsonbProperty("api_status")
    private String apiStatus;

    public RecipeResponse() {}

    public RecipeResponse(boolean success, String apiFailed, String apiStatus) {
        this.success = success;
        this.apiFailed = apiFailed;
        this.apiStatus = apiStatus;
    }

    public RecipeResponse(boolean success, String name, String type, String country, String preparationTime,
                          String imageURL, String source, String ingredients,
                          List<DetailedIngredient> detailedIngredients, List<String> instructions) {
        this.success = success;
        this.name = name;
        this.type = type;
        this.country = country;
        this.preparationTime = preparationTime;
        this.imageURL = imageURL;
        this.source = source;
        this.ingredients = ingredients;
        this.detailedIngredients = detailedIngredients;
        this.instructions = instructions;
    }

    public boolean isSuccess() { return success; }
    public String getApiFailed() { return apiFailed; }
    public String getApiStatus() { return apiStatus; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCountry() { return country; }
    public String getPreparationTime() { return preparationTime; }
    public String getImageURL() { return imageURL; }
    public String getSource() { return source; }
    public String getIngredients() { return ingredients; }
    public List<DetailedIngredient> getDetailedIngredients() { return detailedIngredients; }
    public List<String> getInstructions() { return instructions; }
}
