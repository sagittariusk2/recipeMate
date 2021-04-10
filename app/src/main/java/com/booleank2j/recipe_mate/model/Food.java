package com.booleank2j.recipe_mate.model;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;

public class Food implements Serializable {
    public String foodId, imageUrl, foodName, veg, category;
    public Vector<Pair<String, String>> ingredientsList, howToMake;

    public Food(String foodId) {
        this.foodId = foodId;
    }

    public Food(String foodId, String imageUrl, String foodName, String category, String veg, Vector<Pair<String, String>> ingredientsList, Vector<Pair<String, String>> howToMake) {
        this.foodId = foodId;
        this.imageUrl = imageUrl;
        this.category = category;
        this.foodName = foodName;
        this.veg = veg;
        this.ingredientsList = ingredientsList;
        this.howToMake = howToMake;
    }

    public Food() {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getVeg() {
        return veg;
    }

    public void setVeg(String veg) {
        this.veg = veg;
    }

    public Vector<Pair<String, String>> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(Vector<Pair<String, String>> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public Vector<Pair<String, String>> getHowToMake() {
        return howToMake;
    }

    public void setHowToMake(Vector<Pair<String, String>> howToMake) {
        this.howToMake = howToMake;
    }
}
