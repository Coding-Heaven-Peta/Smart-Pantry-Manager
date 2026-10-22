package za.co.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Recipe {

    private long id;
    private String name;
    private String description;
    private int servings;
    private String steps;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String name, String description, int servings, String steps,
                  List<RecipeIngredient> ingredients) {
        this.name = name;
        this.description = description;
        this.servings = servings;
        this.steps = steps;
        if (ingredients != null) {
            this.ingredients.addAll(ingredients);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }

    public void setIngredients(List<RecipeIngredient> newIngredients) {
        ingredients.clear();
        if (newIngredients != null) {
            ingredients.addAll(newIngredients);
        }
    }

    public List<String> getStepList() {
        List<String> result = new ArrayList<>();
        if (steps == null) {
            return result;
        }
        for (String line : steps.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
