package maar.project.meal.json;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({
        "name",
        "quantity",
        "image"
})
public class DetailedIngredient {

    @JsonbProperty("name")
    private String name;

    @JsonbProperty("quantity")
    private String quantity;

    @JsonbProperty("image")
    private String image;

    public DetailedIngredient() {}

    public DetailedIngredient(String name, String quantity, String image) {
        this.name = name;
        this.quantity = quantity;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }
}
