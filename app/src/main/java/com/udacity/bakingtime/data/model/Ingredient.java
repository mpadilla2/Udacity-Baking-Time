package com.udacity.bakingtime.data.model;

// todo modify toString method to return the proper format that I want to display in a textview

public class Ingredient{

    private double quantity;
    private String measure;
    private String ingredient;


    public Ingredient(double quantity, String measure, String ingredient) {
        super();
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Override
    public String toString() {
        /*       return new ToStringBuilder(this)
               .append(getQuantity())
               .append(" ").append(getMeasure())
               .append(" ").append(getIngredient())
               .toString();*/
        return getQuantity() + " " + getMeasure() + " " + getIngredient() + "\n";
    }
}
