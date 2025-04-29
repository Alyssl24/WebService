package maar.project.menu.json;

import java.util.List;

public class MenuRequest {
    private String cuisineType;
    private Boolean alcohol;
    private String requiredIngredient;
    private Integer maxPreparationTime;
    private List<String> constraints;

    public String getCuisineType() {
        return cuisineType;
    }

    public Boolean getAlcohol() {
        return alcohol;
    }

    public String getRequiredIngredient() {
        return requiredIngredient;
    }

    public Integer getMaxPreparationTime() {
        return maxPreparationTime;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public void setAlcohol(Boolean alcohol) {
        this.alcohol = alcohol;
    }

    public void setRequiredIngredient(String requiredIngredient) {
        this.requiredIngredient = requiredIngredient;
    }

    public void setMaxPreparationTime(Integer maxPreparationTime) {
        this.maxPreparationTime = maxPreparationTime;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }
}
