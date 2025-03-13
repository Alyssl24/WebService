package maar.project.meal;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ingredients_Type")
@XmlRootElement(name = "ingredients") // L'élément racine dans le XML
public class Ingredients {

    @XmlElement(name = "ingredient") // Chaque élément de la liste devient un <ingredient>
    private List<Ingredient> ingredientList;

    public Ingredients() {}

    public Ingredients(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }
}
