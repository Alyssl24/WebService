package maar.project.recette;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"fullText", "pureName", "quantity", "imageIngredient"})
public class Ingredient {

    @XmlElement(name = "full_text")
    private String fullText;

    @XmlElement(name = "pure_name")
    private String pureName;

    @XmlElement(name = "quantity")
    private String quantity;

    @XmlElement(name = "image_ingredient", nillable = true)
    private String imageIngredient;

    public Ingredient() {}

    public Ingredient(String fullText, String pureName, String quantity, String imageIngredient) {
        this.fullText = fullText;
        this.pureName = pureName;
        this.quantity = quantity;
        this.imageIngredient = imageIngredient;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPureName() {
        return pureName;
    }

    public void setPureName(String pureName) {
        this.pureName = pureName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImageIngredient() {
        return imageIngredient;
    }

    public void setImageIngredient(String imageIngredient) {
        this.imageIngredient = imageIngredient;
    }
}
