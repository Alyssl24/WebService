package maar.project.recette;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"mealTypes", "dishTypes", "cuisineTypes"})
public class RecipeDetails {

    @XmlElementWrapper(name = "meal_types")
    @XmlElement(name = "meal_type")
    private List<String> mealTypes;

    @XmlElementWrapper(name = "dish_types")
    @XmlElement(name = "dish_type")
    private List<String> dishTypes;

    @XmlElementWrapper(name = "cuisine_types")
    @XmlElement(name = "kitchen_type")
    private List<String> cuisineTypes;

    public RecipeDetails() {}

    public RecipeDetails(List<String> mealTypes, List<String> dishTypes, List<String> cuisineTypes) {
        this.mealTypes = mealTypes;
        this.dishTypes = dishTypes;
        this.cuisineTypes = cuisineTypes;
    }

    public List<String> getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(List<String> mealTypes) {
        this.mealTypes = mealTypes;
    }

    public List<String> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<String> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }
}
