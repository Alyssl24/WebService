package maar.project.recette;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"texteComplet", "nomPur", "quantite", "imageIngredient"})
public class Ingredient {

    private String texteComplet;
    private String nomPur;
    private String quantite;
    private String imageIngredient;

    public Ingredient() {}

    public Ingredient(String texteComplet, String nomPur, String quantite, String imageIngredient) {
        this.texteComplet = texteComplet;
        this.nomPur = nomPur;
        this.quantite = quantite;
        this.imageIngredient = imageIngredient;
    }

    @XmlElement(name = "texte_complet")
    public String getTexteComplet() {
        return texteComplet;
    }

    public void setTexteComplet(String texteComplet) {
        this.texteComplet = texteComplet;
    }

    @XmlElement(name = "nom_pur")
    public String getNomPur() {
        return nomPur;
    }

    public void setNomPur(String nomPur) {
        this.nomPur = nomPur;
    }

    @XmlElement(name = "quantite")
    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }

    @XmlElement(name = "image_ingredient")
    public String getImageIngredient() {
        return imageIngredient;
    }

    public void setImageIngredient(String imageIngredient) {
        this.imageIngredient = imageIngredient;
    }
}
