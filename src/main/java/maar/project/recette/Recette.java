package maar.project.recette;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;

@XmlRootElement(name = "recette", namespace = "http://www.monsite.com/recettes/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recette {

    @XmlAttribute(name = "id_recette", required = true)
    private String idRecette;

    @XmlElement(name = "nom_plat", namespace = "http://www.monsite.com/recettes/v1")
    private String nomPlat;

    @XmlElement(name = "type_details", namespace = "http://www.monsite.com/recettes/v1")
    private TypeDetails typeDetails;

    @XmlElement(name = "temps_preparation", namespace = "http://www.monsite.com/recettes/v1")
    private String tempsPreparation; // Format HH:mm:ss

    @XmlElement(name = "image_recette", namespace = "http://www.monsite.com/recettes/v1")
    private String imageRecette;

    @XmlElement(name = "url_origine", namespace = "http://www.monsite.com/recettes/v1")
    private String urlOrigine;

    @XmlElement(name = "ingredients", namespace = "http://www.monsite.com/recettes/v1")
    private Ingredients ingredients;

    @XmlElement(name = "allergenes", namespace = "http://www.monsite.com/recettes/v1")
    private Allergene allergenes;

    @XmlElement(name = "calories", namespace = "http://www.monsite.com/recettes/v1")
    private BigDecimal calories;

    public Recette() {}

    public Recette(String idRecette, String nomPlat, TypeDetails typeDetails, String tempsPreparation,
                   String imageRecette, String urlOrigine, Ingredients ingredients,
                   Allergene allergenes, BigDecimal calories) {
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
}
