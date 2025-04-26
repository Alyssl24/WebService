package maar.project.menu.json;

import maar.project.drinks.json.DrinkResponse;
import maar.project.meal.json.RecipeResponse;

public class MenuResponse {
    private RecipeResponse entree;
    private RecipeResponse plat;
    private RecipeResponse dessert;
    private DrinkResponse boisson;
    private int preparationTime;
    private int totalCalories;

    // Getters et Setters
    public RecipeResponse getEntree() { return entree; }
    public void setEntree(RecipeResponse entree) { this.entree = entree; }

    public RecipeResponse getPlat() { return plat; }
    public void setPlat(RecipeResponse plat) { this.plat = plat; }

    public RecipeResponse getDessert() { return dessert; }
    public void setDessert(RecipeResponse dessert) { this.dessert = dessert; }

    public DrinkResponse getBoisson() { return boisson; }
    public void setBoisson(DrinkResponse boisson) { this.boisson = boisson; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public int getTotalCalories() { return totalCalories; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }
}
