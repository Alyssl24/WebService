package maar.project.meal.xml;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Meal_Types")
@XmlRootElement(name = "meal_types")
public class MealTypes {

    @JsonbProperty("type")
    @XmlElement(name = "meal_type")
    private List<String> mealTypes;

    public MealTypes() {}

    public MealTypes(List<String> mealTypes) {
        this.mealTypes = mealTypes;
    }

    public List<String> getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(List<String> mealTypes) {
        this.mealTypes = mealTypes;
    }
}
