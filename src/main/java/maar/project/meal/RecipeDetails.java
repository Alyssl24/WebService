package maar.project.meal;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"mealTypes", "dishTypes", "cuisineTypes"})
public class RecipeDetails {

    @XmlElement(name = "meal_types")
    private MealTypes mealTypes; // Utilisation de la classe MealTypes

    @XmlElement(name = "dish_types")
    private DishTypes dishTypes; // Utilisation de la classe DishTypes

    @XmlElementWrapper(name = "cuisine_types")
    @XmlElement(name = "kitchen_type")
    private List<String> cuisineTypes;

    public RecipeDetails() {}

    public RecipeDetails(MealTypes mealTypes, DishTypes dishTypes, List<String> cuisineTypes) {
        this.mealTypes = mealTypes;
        this.dishTypes = dishTypes;
        this.cuisineTypes = cuisineTypes;
    }

    public MealTypes getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(MealTypes mealTypes) {
        this.mealTypes = mealTypes;
    }

    public DishTypes getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(DishTypes dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }
}
