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

}
