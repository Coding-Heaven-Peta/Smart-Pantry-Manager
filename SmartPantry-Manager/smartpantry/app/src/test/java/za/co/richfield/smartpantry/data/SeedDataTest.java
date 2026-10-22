package za.co.richfield.smartpantry.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import za.co.richfield.smartpantry.logic.RecipeMatcher;
import za.co.richfield.smartpantry.model.MatchResult;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SeedDataTest {

    @Test
    public void recipeCollectionHasTheRequiredSize() {
        List<Recipe> recipes = DatabaseSeeder.buildSeedRecipes();
        assertTrue("The brief requires at least 15 recipes", recipes.size() >= 15);
        assertTrue("The brief allows up to 20 recipes", recipes.size() <= 20);
    }

    @Test
    public void everyRecipeIsComplete() {
        for (Recipe recipe : DatabaseSeeder.buildSeedRecipes()) {
            assertTrue(recipe.getName() + " needs a name", recipe.getName().length() > 2);
            assertFalse(recipe.getName() + " needs a description",
                    recipe.getDescription() == null || recipe.getDescription().isEmpty());
            assertTrue(recipe.getName() + " needs at least 2 ingredients",
                    recipe.getIngredients().size() >= 2);
            assertTrue(recipe.getName() + " needs method steps",
                    recipe.getStepList().size() >= 3);
            assertTrue(recipe.getName() + " needs servings", recipe.getServings() > 0);
        }
    }

    @Test
    public void demoPantryProducesBothStrictAndAlmostMatches() {
        List<Recipe> recipes = DatabaseSeeder.buildSeedRecipes();
        List<PantryItem> pantry = DatabaseSeeder.buildDemoPantry();

        List<MatchResult> strict = RecipeMatcher.strictMatches(recipes, pantry);
        List<MatchResult> almost = RecipeMatcher.almostMatches(recipes, pantry);

        assertTrue("The demo pantry should allow several recipes", strict.size() >= 5);
        assertTrue("The demo pantry should leave some recipes one ingredient short",
                almost.size() >= 3);

        System.out.println("Demo pantry: " + pantry.size() + " items, "
                + strict.size() + " cookable, " + almost.size() + " almost there");
        for (MatchResult match : strict) {
            System.out.println("  COOK NOW: " + match.getRecipe().getName());
        }
        for (MatchResult match : almost) {
            System.out.println("  ALMOST:   " + match.getRecipe().getName()
                    + " -> missing " + match.getMissing().get(0).getRequired().getName());
        }
    }

    @Test
    public void removingCheeseRemovesCheesyRecipesFromTheSuggestions() {
        List<Recipe> recipes = DatabaseSeeder.buildSeedRecipes();
        List<PantryItem> pantry = new ArrayList<>(DatabaseSeeder.buildDemoPantry());

        List<MatchResult> before = RecipeMatcher.strictMatches(recipes, pantry);

        PantryItem cheese = null;
        for (PantryItem item : pantry) {
            if ("Cheese".equals(item.getName())) {
                cheese = item;
            }
        }
        assertTrue("Demo pantry must contain cheese", cheese != null);
        pantry.remove(cheese);

        List<MatchResult> after = RecipeMatcher.strictMatches(recipes, pantry);

        assertTrue("Removing one ingredient must shrink the suggestion list",
                after.size() < before.size());
        assertFalse("Cheesy Garlic Bread must no longer be suggested",
                containsRecipe(after, "Cheesy Garlic Bread"));
        assertFalse("Mac and Cheese must no longer be suggested",
                containsRecipe(after, "Mac and Cheese"));
        assertEquals("The item count is unchanged; only cheese was removed",
                DatabaseSeeder.buildDemoPantry().size() - 1, pantry.size());
    }

    private boolean containsRecipe(List<MatchResult> results, String recipeName) {
        for (MatchResult result : results) {
            if (recipeName.equals(result.getRecipe().getName())) {
                return true;
            }
        }
        return false;
    }
}
