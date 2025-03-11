package maar.project.drinks;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class IngredientDetails {

    @XmlElement(name = "ingredient")
    private List<IngredientDrink> ingredients;

    public IngredientDetails() {}

    public IngredientDetails(List<IngredientDrink> ingredients) {
        this.ingredients = ingredients;
    }

    // Getters et Setters
}

