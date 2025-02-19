package maar.project.recette;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "type_details", namespace = "http://www.monsite.com/recettes/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeDetails {

    @XmlElement(name = "type_repas", namespace = "http://www.monsite.com/recettes/v1")
    private String typeRepas;

    @XmlElement(name = "type_plat", namespace = "http://www.monsite.com/recettes/v1")
    private String typePlat;

    @XmlElement(name = "type_cuisine", namespace = "http://www.monsite.com/recettes/v1")
    private String typeCuisine;

    public TypeDetails() {}

    public TypeDetails(String typeRepas, String typePlat, String typeCuisine) {
        this.typeRepas = typeRepas;
        this.typePlat = typePlat;
        this.typeCuisine = typeCuisine;
    }
}
