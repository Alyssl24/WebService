package maar.project.drinks.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class IngredientDrink {
    @XmlElement(name = "full_text")
    private String fullText;

    @XmlElement(name = "pure_name")
    private String pureName;

    @XmlElement(name = "quantity")
    private String quantity;

    public IngredientDrink() {}

    public IngredientDrink(String fullText, String pureName, String quantity) {
        this.fullText = fullText;
        this.pureName = pureName;
        this.quantity = quantity;
    }

}
