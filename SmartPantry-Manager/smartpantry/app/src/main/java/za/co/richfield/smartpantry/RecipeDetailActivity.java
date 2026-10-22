package za.co.richfield.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import za.co.richfield.smartpantry.data.DatabaseHelper;
import za.co.richfield.smartpantry.databinding.ActivityRecipeDetailBinding;
import za.co.richfield.smartpantry.logic.RecipeMatcher;
import za.co.richfield.smartpantry.logic.UnitConverter;
import za.co.richfield.smartpantry.model.IngredientCheck;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.model.RecipeIngredient;

// Full Recipe with ingredients and steps
public class RecipeDetailActivity extends AppCompatActivity {

    private static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private ActivityRecipeDetailBinding binding;
    private DatabaseHelper databaseHelper;

    public static Intent createIntent(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        Recipe recipe = databaseHelper.getRecipeById(recipeId);
        if (recipe == null) {
            Toast.makeText(this, R.string.empty_suggestions_title, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.textRecipeName.setText(recipe.getName());
        binding.textRecipeDescription.setText(recipe.getDescription());
        binding.textRecipeMeta.setText(getString(R.string.recipe_meta,
                recipe.getServings(), recipe.getIngredients().size()));

        List<PantryItem> pantry = databaseHelper.getAllPantryItems();
        bindIngredients(recipe, pantry);
        bindSteps(recipe);
    }

    private void bindIngredients(Recipe recipe, List<PantryItem> pantry) {
        binding.layoutIngredients.removeAllViews();
        for (RecipeIngredient required : recipe.getIngredients()) {
            IngredientCheck check = RecipeMatcher.checkIngredient(required, pantry);
            binding.layoutIngredients.addView(createIngredientRow(required, check));
        }
    }

    private LinearLayout createIngredientRow(RecipeIngredient required, IngredientCheck check) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(7), 0, dp(7));

        TextView status = new TextView(this);
        status.setText(check.isSatisfied() ? "\u2713" : "\u2717");
        status.setTextSize(16);
        status.setTypeface(Typeface.DEFAULT_BOLD);
        status.setTextColor(ContextCompat.getColor(this,
                check.isSatisfied() ? R.color.brand_green : R.color.danger_red));
        status.setWidth(dp(28));
        row.addView(status);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        texts.setLayoutParams(textsParams);

        TextView name = new TextView(this);
        name.setText(required.getName());
        name.setTextSize(15);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        texts.addView(name);

        TextView detail = new TextView(this);
        String amount = UnitConverter.formatQuantity(
                UnitConverter.toBaseUnit(required.getQuantity(), required.getUnit()),
                required.getUnit());
        detail.setText(amount + " needed  \u00B7  " + check.getPantrySummary());
        detail.setTextSize(12);
        detail.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        texts.addView(detail);

        row.addView(texts);
        return row;
    }

    private void bindSteps(Recipe recipe) {
        binding.layoutSteps.removeAllViews();
        List<String> steps = recipe.getStepList();
        for (int index = 0; index < steps.size(); index++) {
            TextView step = new TextView(this);
            step.setText(getString(R.string.step_format, index + 1, steps.get(index)));
            step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            step.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            step.setLineSpacing(dp(4), 1f);
            step.setPadding(0, dp(4), 0, dp(10));
            binding.layoutSteps.addView(step);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
