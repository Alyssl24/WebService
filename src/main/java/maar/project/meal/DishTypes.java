package maar.project.meal;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Dish_Types")
@XmlRootElement(name = "dish_types")
public class DishTypes {

    @XmlElement(name = "dish_type")
    private List<String> dishTypes;

    public DishTypes() {}

    public DishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }
}
