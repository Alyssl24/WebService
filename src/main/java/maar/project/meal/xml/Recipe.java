package maar.project.meal.xml;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "recipe")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recipe {

    @XmlAttribute(name = "recipe_id", required = true)
    private String recipeId;

    @JsonbProperty("name")
    @XmlElement(name = "name_recipe")
    private String nomPlat;

    @XmlElement(name = "type_details")
    private RecipeDetails typeDetails;

    @JsonbProperty("preparationTime")
    @XmlElement(name = "preparation_time")
    private String preparationTime;

    @JsonbProperty("imageURL")
    @XmlElement(name = "recipe_image")
    private String imageRecette;

    @JsonbProperty("source")
    @XmlElement(name = "source_url")
    private String urlOrigine;

    @XmlElement(name = "calories")
    private BigDecimal calories;

    @XmlElement(name = "allergens")
    private String allergenes;

    @XmlElementWrapper(name = "ingredients")
    @XmlElement(name = "ingredient")
    private List<Ingredient> ingredients;

    public Recipe() {}

    public Recipe(String recipeId, String nomPlat, RecipeDetails typeDetails,
                  String preparationTime, String imageRecette, String urlOrigine,
                  List<Ingredient> ingredients, String allergenes, BigDecimal calories) {
        this.recipeId = recipeId;
        this.nomPlat = nomPlat;
        this.typeDetails = typeDetails;
        this.preparationTime = preparationTime;
        this.imageRecette = imageRecette;
        this.urlOrigine = urlOrigine;
        this.ingredients = ingredients;
        this.allergenes = allergenes;
        this.calories = calories;
    }

    // Getters & Setters
    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }
}
