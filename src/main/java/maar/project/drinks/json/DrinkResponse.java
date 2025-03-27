package maar.project.drinks.json;

import java.util.List;

public class DrinkResponse {
    private boolean success;
    private String api_failed;
    private String api_status;
    private String name;
    private String type;
    private boolean alcoholic;
    private String imageURL;
    private List<String> ingredients;
    private List<DetailedIngredient> detailedIngredients;
    private String instructions;

    // Getters & setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getApi_failed() { return api_failed; }
    public void setApi_failed(String api_failed) { this.api_failed = api_failed; }

    public String getApi_status() { return api_status; }
    public void setApi_status(String api_status) { this.api_status = api_status; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isAlcoholic() { return alcoholic; }
    public void setAlcoholic(boolean alcoholic) { this.alcoholic = alcoholic; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<DetailedIngredient> getDetailedIngredients() { return detailedIngredients; }
    public void setDetailedIngredients(List<DetailedIngredient> detailedIngredients) { this.detailedIngredients = detailedIngredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public static class DetailedIngredient {
        private String name;
        private String quantity;
        private String image;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }
}
