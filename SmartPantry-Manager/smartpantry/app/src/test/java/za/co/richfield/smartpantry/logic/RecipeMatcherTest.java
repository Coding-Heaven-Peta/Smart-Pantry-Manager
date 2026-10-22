package za.co.richfield.smartpantry.logic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.co.richfield.smartpantry.model.IngredientCheck;
import za.co.richfield.smartpantry.model.MatchResult;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.model.RecipeIngredient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeMatcherTest {

    private static PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, "");
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setIngredients(new ArrayList<>(Arrays.asList(ingredients)));
        return recipe;
    }

    @Test
    public void recipeWithAllIngredientsIsASupportedMatch() {
        Recipe pancakes = recipe("Pancakes",
                ing("Flour", 250, "g"),
                ing("Milk", 300, "ml"),
                ing("Egg", 2, "piece"));

        List<PantryItem> pantry = Arrays.asList(
                item("Flour", 1, "kg"),
                item("Milk", 1, "L"),
                item("Eggs", 6, "piece"));

        MatchResult result = RecipeMatcher.check(pancakes, pantry);
        assertTrue(result.isFullMatch());
        assertEquals(0, result.getMissingCount());
    }

    @Test
    public void recipeMissingOneIngredientIsNotSuggested() {
        // Five ingredients, four in the pantry: this MUST NOT appear in the strict list.
        Recipe recipe = recipe("Mac and Cheese",
                ing("Pasta", 250, "g"),
                ing("Milk", 300, "ml"),
                ing("Butter", 30, "g"),
                ing("Flour", 25, "g"),
                ing("Cheese", 150, "g"));

        List<PantryItem> pantry = Arrays.asList(
                item("Pasta", 500, "g"),
                item("Milk", 1, "L"),
                item("Butter", 250, "g"),
                item("Flour", 1, "kg"));

        MatchResult result = RecipeMatcher.check(recipe, pantry);

        assertFalse("Recipe with a missing ingredient must not be a full match", result.isFullMatch());
        assertEquals(1, result.getMissingCount());
        assertEquals("Cheese", result.getMissing().get(0).getRequired().getName());
        assertTrue(result.isAlmostMatch());

        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(recipe), pantry).isEmpty());
        assertEquals(1, RecipeMatcher.almostMatches(Arrays.asList(recipe), pantry).size());
    }

    @Test
    public void quantitiesAreComparedAcrossUnits() {
        Recipe cake = recipe("Mug Cake", ing("Butter", 500, "g"));
        List<PantryItem> pantry = Arrays.asList(item("Butter", 0.5, "kg"));

        assertTrue("0.5 kg must satisfy a 500 g requirement",
                RecipeMatcher.check(cake, pantry).isFullMatch());
    }

    @Test
    public void insufficientQuantityIsNotAMatch() {
        Recipe cake = recipe("Mug Cake", ing("Cheese", 150, "g"));
        List<PantryItem> pantry = Arrays.asList(item("Cheese", 200, "g"));

        MatchResult result = RecipeMatcher.check(cake, pantry);
        assertTrue("200 g is enough for a 150 g requirement", result.isFullMatch());

        Recipe biggerCake = recipe("Big Cake", ing("Cheese", 300, "g"));
        assertFalse("200 g is not enough for a 300 g requirement",
                RecipeMatcher.check(biggerCake, pantry).isFullMatch());
    }

    @Test
    public void pluralAndCapitalisationDifferencesStillMatch() {
        Recipe salad = recipe("Salad",
                ing("Tomatoes", 2, "piece"),
                ing("Onions", 1, "piece"));
        List<PantryItem> pantry = Arrays.asList(
                item("tomato", 3, "piece"),
                item("ONION", 2, "piece"));

        assertTrue(RecipeMatcher.check(salad, pantry).isFullMatch());
    }

    @Test
    public void quantitiesOfTheSameIngredientAreAddedTogether() {
        Recipe sauce = recipe("Sauce", ing("Butter", 100, "g"));
        List<PantryItem> pantry = Arrays.asList(
                item("Butter", 60, "g"),
                item("butter", 50, "g"));

        assertTrue("60 g + 50 g must satisfy a 100 g requirement",
                RecipeMatcher.check(sauce, pantry).isFullMatch());
    }

    @Test
    public void differentMeasurementFamiliesDoNotCountAsAMatch() {
        Recipe dish = recipe("Cheesy Dish", ing("Cheese", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(item("Cheese", 2, "piece"));

        IngredientCheck check = RecipeMatcher.checkIngredient(dish.getIngredients().get(0), pantry);
        assertFalse(check.isSatisfied());
        assertTrue(check.isUnitMismatch());
    }

    @Test
    public void emptyPantryMatchesNothing() {
        Recipe recipe = recipe("Toast", ing("Bread", 2, "piece"), ing("Butter", 20, "g"));
        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(recipe), new ArrayList<>()).isEmpty());
        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(recipe), null).isEmpty());
    }

    @Test
    public void strictAndAlmostListsAreSeparated() {
        Recipe cookable = recipe("Toast", ing("Bread", 2, "piece"));
        Recipe almost = recipe("Cheese Toast", ing("Bread", 2, "piece"), ing("Cheese", 50, "g"));
        Recipe farOff = recipe("Full Breakfast",
                ing("Bread", 2, "piece"), ing("Cheese", 50, "g"), ing("Egg", 2, "piece"));

        List<PantryItem> pantry = Arrays.asList(item("Bread", 8, "piece"));
        List<Recipe> recipes = Arrays.asList(cookable, almost, farOff);

        List<MatchResult> strict = RecipeMatcher.strictMatches(recipes, pantry);
        List<MatchResult> almostList = RecipeMatcher.almostMatches(recipes, pantry);

        assertEquals(1, strict.size());
        assertEquals("Toast", strict.get(0).getRecipe().getName());

        assertEquals(1, almostList.size());
        assertEquals("Cheese Toast", almostList.get(0).getRecipe().getName());
    }
}
