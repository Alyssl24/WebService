package maar.project.drinks.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DrinkDetails {

    @XmlElement(name = "contains_alcohol")
    private boolean containsAlcohol;

    @XmlElement(name = "drink_category")
    private String drinkCategory;

    @XmlElement(name = "glass_type")
    private String glassType;

    public DrinkDetails() {}

    public DrinkDetails(boolean containsAlcohol, String drinkCategory, String glassType) {
        this.containsAlcohol = containsAlcohol;
        this.drinkCategory = drinkCategory;
        this.glassType = glassType;
    }

}
