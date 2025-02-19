package maar.project.recette;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "allergenes", namespace = "http://www.monsite.com/recettes/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Allergene {

    @XmlElement(name = "allergene", namespace = "http://www.monsite.com/recettes/v1")
    private List<String> allergenes;

    public Allergene() {}

    public Allergene(List<String> allergenes) {
        this.allergenes = allergenes;
    }
}