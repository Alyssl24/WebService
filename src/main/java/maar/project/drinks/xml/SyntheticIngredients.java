package maar.project.drinks.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class SyntheticIngredients {

    @XmlElement(name = "synthetic_ingredient")
    private List<String> syntheticIngredient;

    public SyntheticIngredients() {}

    public SyntheticIngredients(List<String> syntheticIngredient) {
        this.syntheticIngredient = syntheticIngredient;
    }
}

