package maar.project.recette;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlRootElement(name = "ingredients", namespace = "http://www.monsite.com/recettes/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ingredients {

    @XmlElement(name = "ingredient", namespace = "http://www.monsite.com/recettes/v1")
    private List<Ingredient> ingredientList;

    public Ingredients() {}

    public Ingredients(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }
}