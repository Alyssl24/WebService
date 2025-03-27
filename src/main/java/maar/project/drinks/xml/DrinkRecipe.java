package maar.project.drinks.xml;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "drink_recipe", namespace = "http://www.mysite.com/drinks/v1")
@XmlType(namespace = "http://www.mysite.com/drinks/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrinkRecipe {

    @XmlAttribute(name = "recipe_id", required = true)
    private String recipeId;

    @XmlElement(name = "drink_name")
    private String drinkName;

    @XmlElement(name = "drink_details")
    private DrinkDetails drinkDetails;

    @XmlElement(name = "drink_image")
    private String drinkImage;

    @XmlElement(name = "synthetic_ingredients")
    private SyntheticIngredients syntheticIngredients;

    @XmlElement(name = "ingredient_details")
    private IngredientDetails ingredientDetails;

    @XmlElement(name = "instructions")
    private String instructions;

    public DrinkRecipe() {}

    public DrinkRecipe(String recipeId, String drinkName, DrinkDetails drinkDetails,
                       String drinkImage, SyntheticIngredients syntheticIngredients,
                       IngredientDetails ingredientDetails, String instructions) {
        this.recipeId = recipeId;
        this.drinkName = drinkName;
        this.drinkDetails = drinkDetails;
        this.drinkImage = drinkImage;
        this.syntheticIngredients = syntheticIngredients;
        this.ingredientDetails = ingredientDetails;
        this.instructions = instructions;
    }
}
