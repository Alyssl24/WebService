package maar.project.recette;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ingredient", namespace = "http://www.monsite.com/recettes/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ingredient {

    @XmlElement(name = "texte_complet", namespace = "http://www.monsite.com/recettes/v1")
    private String texteComplet;

    @XmlElement(name = "nom_pur", namespace = "http://www.monsite.com/recettes/v1")
    private String nomPur;

    @XmlElement(name = "quantite", namespace = "http://www.monsite.com/recettes/v1")
    private String quantite;

    @XmlElement(name = "image_ingredient", namespace = "http://www.monsite.com/recettes/v1")
    private String imageIngredient;

    public Ingredient() {}

    public Ingredient(String texteComplet, String nomPur, String quantite, String imageIngredient) {
        this.texteComplet = texteComplet;
        this.nomPur = nomPur;
        this.quantite = quantite;
        this.imageIngredient = imageIngredient;
    }
}