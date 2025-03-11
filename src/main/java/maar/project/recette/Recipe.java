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
        this.allergenes = allergenes;
        this.calories = calories;
    }

    // Getters & Setters
    public String getRecipeId() {
        return RecipeId;
    }

    public void setRecipeId(String idRecette) {
        this.RecipeId = idRecette;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public RecipeDetails getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(RecipeDetails typeDetails) {
        this.typeDetails = typeDetails;
    }

    public String getTempsPreparation() {
        return tempsPreparation;
    }

    public void setTempsPreparation(String tempsPreparation) {
        this.tempsPreparation = tempsPreparation;
    }

    public String getImageRecette() {
        return imageRecette;
    }

    public void setImageRecette(String imageRecette) {
        this.imageRecette = imageRecette;
    }

    public String getUrlOrigine() {
        return urlOrigine;
    }

    public void setUrlOrigine(String urlOrigine) {
        this.urlOrigine = urlOrigine;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public String getAllergenes() {
        return allergenes;
    }

    public void setAllergenes(String allergenes) {
        this.allergenes = allergenes;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
