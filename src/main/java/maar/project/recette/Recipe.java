package maar.project.recette;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "recipe")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recipe {

    @XmlAttribute(name = "recipe_id", required = true)
    private String RecipeId;

    @XmlElement(name = "name_recipe")
    private String nomPlat;

    @XmlElement(name = "type_details")
    private RecipeDetails typeDetails;

    @XmlElement(name = "temps_preparation")
    private String tempsPreparation;

    @XmlElement(name = "image_recette")
    private String imageRecette;

    @XmlElement(name = "url_origine")
    private String urlOrigine;

    @XmlElement(name = "calories")
    private BigDecimal calories;

    @XmlElement(name = "allergenes")
    private String allergenes;

    @XmlElementWrapper(name = "ingredients")
    @XmlElement(name = "ingredient")
    private List<Ingredient> ingredients;

    public Recipe() {}

    public Recipe(String RecipeId, String nomPlat, RecipeDetails typeDetails,
                  String tempsPreparation, String imageRecette, String urlOrigine,
                  List<Ingredient> ingredients, String allergenes, BigDecimal calories) {
        this.RecipeId = RecipeId;
        this.nomPlat = nomPlat;
        this.typeDetails = typeDetails;
        this.tempsPreparation = tempsPreparation;
        this.imageRecette = imageRecette;
        this.urlOrigine = urlOrigine;
        this.ingredients = ingredients;
        this.allergenes = allergenes; // ✅ Assignation des allergènes
        this.calories = calories;
    }

    // Getters & Setters
    public String getAllergenes() {
        return allergenes;
    }

    public void setAllergenes(String allergenes) {
        this.allergenes = allergenes;
    }
}
