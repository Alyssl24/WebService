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

    @XmlElement(name = "type_repas")
    private String typeRepas;

    @XmlElement(name = "type_plat")
    private String typePlat;

    @XmlElement(name = "type_cuisine")
    private String typeCuisine;

    @XmlElement(name = "temps_preparation")
    private String tempsPreparation;

    @XmlElement(name = "image_recette")
    private String imageRecette;

    @XmlElement(name = "url_origine")
    private String urlOrigine;

    @XmlElement(name = "calories")
    private BigDecimal calories;

    @XmlElement(name = "text_complet")
    private String textComplet;

    @XmlElement(name = "nom_pur")
    private String nomPur;

    @XmlElement(name = "quantite")
    private String quantite;

    @XmlElement(name = "image_ingredient")
    private String imageIngredient;

    @XmlElement(name = "allergene")
    private String allergene;

    @XmlElementWrapper(name = "ingredients")
    @XmlElement(name = "ingredient")
    private List<String> ingredientLines;

    // 🔹 Constructeur vide requis pour JAXB
    public Recette() {}

    public Recette(String idRecette, String nomPlat, String typeRepas, String typePlat, String typeCuisine,
                   String tempsPreparation, String imageRecette, String urlOrigine, BigDecimal calories,
                   String textComplet, String nomPur, String quantite, String imageIngredient, String allergene,
                   List<String> ingredientLines) {
        this.idRecette = idRecette;
        this.nomPlat = nomPlat;
        this.typeRepas = typeRepas;
        this.typePlat = typePlat;
        this.typeCuisine = typeCuisine;
        this.tempsPreparation = tempsPreparation;
        this.imageRecette = imageRecette;
        this.urlOrigine = urlOrigine;
        this.calories = calories;
        this.textComplet = textComplet;
        this.nomPur = nomPur;
        this.quantite = quantite;
        this.imageIngredient = imageIngredient;
        this.allergene = allergene;
        this.ingredientLines = ingredientLines;
    }

    // 🔹 Getters & Setters
    public String getIdRecette() { return idRecette; }
    public void setIdRecette(String idRecette) { this.idRecette = idRecette; }

    public String getNomPlat() { return nomPlat; }
    public void setNomPlat(String nomPlat) { this.nomPlat = nomPlat; }

    public String getTypeRepas() { return typeRepas; }
    public void setTypeRepas(String typeRepas) { this.typeRepas = typeRepas; }

    public String getTypePlat() { return typePlat; }
    public void setTypePlat(String typePlat) { this.typePlat = typePlat; }

    public String getTypeCuisine() { return typeCuisine; }
    public void setTypeCuisine(String typeCuisine) { this.typeCuisine = typeCuisine; }

    public String getTempsPreparation() { return tempsPreparation; }
    public void setTempsPreparation(String tempsPreparation) { this.tempsPreparation = tempsPreparation; }

    public String getImageRecette() { return imageRecette; }
    public void setImageRecette(String imageRecette) { this.imageRecette = imageRecette; }

    public String getUrlOrigine() { return urlOrigine; }
    public void setUrlOrigine(String urlOrigine) { this.urlOrigine = urlOrigine; }

    public BigDecimal getCalories() { return calories; }
    public void setCalories(BigDecimal calories) { this.calories = calories; }

    public String getTextComplet() { return textComplet; }
    public void setTextComplet(String textComplet) { this.textComplet = textComplet; }

    public String getNomPur() { return nomPur; }
    public void setNomPur(String nomPur) { this.nomPur = nomPur; }

    public String getQuantite() { return quantite; }
    public void setQuantite(String quantite) { this.quantite = quantite; }

    public String getImageIngredient() { return imageIngredient; }
    public void setImageIngredient(String imageIngredient) { this.imageIngredient = imageIngredient; }

    public String getAllergene() { return allergene; }
    public void setAllergene(String allergene) { this.allergene = allergene; }

    public List<String> getIngredientLines() { return ingredientLines; }
    public void setIngredientLines(List<String> ingredientLines) { this.ingredientLines = ingredientLines; }
}
