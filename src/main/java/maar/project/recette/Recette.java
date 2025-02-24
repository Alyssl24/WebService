package maar.project.recette;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "recette")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recette {

    @XmlAttribute(name = "id_recette", required = true)
    private String idRecette;

    @XmlElement(name = "nom_plat")
    private String nomPlat;

    @XmlElement(name = "type_details")
    private TypeDetails typeDetails;

    @XmlElement(name = "temps_preparation")
    private String tempsPreparation;

    @XmlElement(name = "image_recette")
    private String imageRecette;

    @XmlElement(name = "url_origine")
    private String urlOrigine;

    @XmlElement(name = "calories")
    private BigDecimal calories;

    @XmlElement(name = "image_ingredient")
    private String imageIngredient;

    @XmlElement(name = "allergenes")
    private String allergenes;

    @XmlElementWrapper(name = "ingredients")
    @XmlElement(name = "ingredient")
    private List<Ingredient> ingredients;

    public Recette() {}

    public Recette(String idRecette, String nomPlat, TypeDetails typeDetails,
                   String tempsPreparation, String imageRecette, String urlOrigine,List<Ingredient> ingredients,
                   String allergenes, BigDecimal calories) {
        this.idRecette = idRecette;
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

    public String getIdRecette() {
        return idRecette;
    }

    public void setIdRecette(String idRecette) {
        this.idRecette = idRecette;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public TypeDetails getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(TypeDetails typeDetails) {
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

    public String getImageIngredient() {
        return imageIngredient;
    }

    public void setImageIngredient(String imageIngredient) {
        this.imageIngredient = imageIngredient;
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
