package maar.project.recette;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"typesRepas", "typesPlat", "typesCuisine"})
public class TypeDetails {

    @XmlElementWrapper(name = "types_repas")
    @XmlElement(name = "type_repas")
    private List<String> typesRepas;

    @XmlElementWrapper(name = "types_plat")
    @XmlElement(name = "type_plat")
    private List<String> typesPlat;

    @XmlElementWrapper(name = "types_cuisine")
    @XmlElement(name = "type_cuisine")
    private List<String> typesCuisine;

    public TypeDetails() {}

    public TypeDetails(List<String> typesRepas, List<String> typesPlat, List<String> typesCuisine) {
        this.typesRepas = typesRepas;
        this.typesPlat = typesPlat;
        this.typesCuisine = typesCuisine;
    }

    // Getters & Setters

    public List<String> getTypesRepas() {
        return typesRepas;
    }

    public void setTypesRepas(List<String> typesRepas) {
        this.typesRepas = typesRepas;
    }

    public List<String> getTypesPlat() {
        return typesPlat;
    }

    public void setTypesPlat(List<String> typesPlat) {
        this.typesPlat = typesPlat;
    }

    public List<String> getTypesCuisine() {
        return typesCuisine;
    }

    public void setTypesCuisine(List<String> typesCuisine) {
        this.typesCuisine = typesCuisine;
    }
}
