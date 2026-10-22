package za.co.richfield.smartpantry.logic;

import java.util.ArrayList;
import java.util.List;

import za.co.richfield.smartpantry.model.IngredientCheck;
import za.co.richfield.smartpantry.model.MatchResult;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.model.RecipeIngredient;


public final class RecipeMatcher {

    private static final double EPSILON = 0.0001;

    private RecipeMatcher() {
        // Utility class: no instances.
    }
    public static List<MatchResult> matchAll(List<Recipe> recipes, List<PantryItem> pantry) {
        List<MatchResult> results = new ArrayList<>();
        if (recipes == null) {
            return results;
        }
        List<PantryItem> safePantry = pantry == null ? new ArrayList<PantryItem>() : pantry;
        for (Recipe recipe : recipes) {
            results.add(check(recipe, safePantry));
        }
        return results;
    }
    public static List<MatchResult> strictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<MatchResult> strict = new ArrayList<>();
        for (MatchResult result : matchAll(recipes, pantry)) {
            if (result.isFullMatch()) {
                strict.add(result);
            }
        }
        return strict;
    }
    public static List<MatchResult> almostMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<MatchResult> almost = new ArrayList<>();
        for (MatchResult result : matchAll(recipes, pantry)) {
            if (result.isAlmostMatch()) {
                almost.add(result);
            }
        }
        return almost;
    }
    public static MatchResult check(Recipe recipe, List<PantryItem> pantry) {
        List<IngredientCheck> checks = new ArrayList<>();
        if (recipe != null) {
            for (RecipeIngredient required : recipe.getIngredients()) {
                checks.add(checkIngredient(required, pantry));
            }
        }
        return new MatchResult(recipe, checks);
    }

    public static IngredientCheck checkIngredient(RecipeIngredient required, List<PantryItem> pantry) {
        String requiredKey = IngredientNameNormalizer.normalize(required.getName());
        double requiredInBaseUnit = UnitConverter.toBaseUnit(required.getQuantity(), required.getUnit());

        double available = 0.0;
        boolean nameFound = false;
        boolean compatibleUnitFound = false;
        boolean incompatibleUnitFound = false;
        String availableWithUnit = "";

        if (pantry != null) {
            for (PantryItem item : pantry) {
                String itemKey = IngredientNameNormalizer.normalize(item.getName());
                if (!itemKey.equals(requiredKey) || itemKey.isEmpty()) {
                    continue;
                }
                nameFound = true;
                if (!UnitConverter.areCompatible(item.getUnit(), required.getUnit())) {
                    incompatibleUnitFound = true;
                    continue;
                }
                compatibleUnitFound = true;
                available += UnitConverter.toBaseUnit(item.getQuantity(), item.getUnit());
            }
        }

        boolean satisfied = compatibleUnitFound && (available + EPSILON) >= requiredInBaseUnit;

        String summary;
        if (!nameFound) {
            summary = "Not in your pantry";
        } else if (!compatibleUnitFound) {
            summary = "Unit mismatch - cannot compare " + required.getUnit();
        } else {
            summary = "You have " + UnitConverter.formatQuantity(available, required.getUnit());
        }

        boolean unitMismatch = nameFound && !compatibleUnitFound && incompatibleUnitFound;
        return new IngredientCheck(required, satisfied, summary, unitMismatch);
    }
}
