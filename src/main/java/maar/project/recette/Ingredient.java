package maar.project.recette;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"texteComplet", "nomPur", "quantite", "imageIngredient"})
public class Ingredient {

    @XmlElement(name = "texte_complet")
    private String texteComplet;

    @XmlElement(name = "nom_pur")
    private String nomPur;

    @XmlElement(name = "quantite")
    private String quantite;

    @XmlElement(name = "image_ingredient")
    private String imageIngredient;

    public Ingredient() {}

    public Ingredient(String texteComplet, String nomPur, String quantite, String imageIngredient) {
        this.texteComplet = texteComplet;
        this.nomPur = nomPur;
        this.quantite = quantite;
        this.imageIngredient = imageIngredient;
    }

    public String getTexteComplet() {
        return texteComplet;
    }

    public void setTexteComplet(String texteComplet) {
        this.texteComplet = texteComplet;
    }

    public String getNomPur() {
        return nomPur;
    }

    public void setNomPur(String nomPur) {
        this.nomPur = nomPur;
    }

    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }

    public String getImageIngredient() {
        return imageIngredient;
    }

    public void setImageIngredient(String imageIngredient) {
        this.imageIngredient = imageIngredient;
    }
}
