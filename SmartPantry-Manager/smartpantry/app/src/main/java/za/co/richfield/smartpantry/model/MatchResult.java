package za.co.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;


public class MatchResult {

    private final Recipe recipe;
    private final List<IngredientCheck> checks;

    public MatchResult(Recipe recipe, List<IngredientCheck> checks) {
        this.recipe = recipe;
        this.checks = checks;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<IngredientCheck> getChecks() {
        return checks;
    }

    public boolean isFullMatch() {
        return getMissing().isEmpty();
    }

    public List<IngredientCheck> getMissing() {
        List<IngredientCheck> missing = new ArrayList<>();
        for (IngredientCheck check : checks) {
            if (!check.isSatisfied()) {
                missing.add(check);
            }
        }
        return missing;
    }

    public List<IngredientCheck> getSatisfied() {
        List<IngredientCheck> satisfied = new ArrayList<>();
        for (IngredientCheck check : checks) {
            if (check.isSatisfied()) {
                satisfied.add(check);
            }
        }
        return satisfied;
    }

    public int getMissingCount() {
        return getMissing().size();
    }

    public boolean isAlmostMatch() {
        return getMissingCount() == 1;
    }
}
