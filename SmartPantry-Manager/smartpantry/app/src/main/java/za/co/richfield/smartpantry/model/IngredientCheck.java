package za.co.richfield.smartpantry.model;


public class IngredientCheck {

    private final RecipeIngredient required;
    private final boolean satisfied;
    private final String pantrySummary;
    private final boolean unitMismatch;

    public IngredientCheck(RecipeIngredient required, boolean satisfied,
                           String pantrySummary, boolean unitMismatch) {
        this.required = required;
        this.satisfied = satisfied;
        this.pantrySummary = pantrySummary;
        this.unitMismatch = unitMismatch;
    }

    public RecipeIngredient getRequired() {
        return required;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public String getPantrySummary() {
        return pantrySummary;
    }

    public boolean isUnitMismatch() {
        return unitMismatch;
    }
}
