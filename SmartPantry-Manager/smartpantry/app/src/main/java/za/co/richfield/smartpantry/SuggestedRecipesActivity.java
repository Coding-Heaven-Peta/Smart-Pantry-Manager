package za.co.richfield.smartpantry;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import za.co.richfield.smartpantry.data.DatabaseHelper;
import za.co.richfield.smartpantry.databinding.ActivitySuggestedRecipesBinding;
import za.co.richfield.smartpantry.logic.RecipeMatcher;
import za.co.richfield.smartpantry.model.MatchResult;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.ui.MatchAdapter;
import za.co.richfield.smartpantry.util.AppPreferences;

// Suggested Recipes
public class SuggestedRecipesActivity extends AppCompatActivity {

    private ActivitySuggestedRecipesBinding binding;
    private DatabaseHelper databaseHelper;
    private AppPreferences preferences;
    private MatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuggestedRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        preferences = new AppPreferences(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new MatchAdapter(recipe ->
                startActivity(RecipeDetailActivity.createIntent(this, recipe.getId())));
        binding.recyclerMatches.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerMatches.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-run the match every time the screen is shown, because the pantry
        // may have changed since the last visit.
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        List<PantryItem> pantry = databaseHelper.getAllPantryItems();
        List<Recipe> recipes = databaseHelper.getAllRecipes();

        List<MatchResult> strictMatches = RecipeMatcher.strictMatches(recipes, pantry);
        List<MatchResult> almostMatches = RecipeMatcher.almostMatches(recipes, pantry);

        // Build the list in display order: a header, then the strict matches,
        // then (optionally) a clearly separated "almost there" section.
        adapter.clear();
        if (!strictMatches.isEmpty()) {
            adapter.addHeader(getString(R.string.section_can_cook, strictMatches.size()));
            for (MatchResult match : strictMatches) {
                adapter.addRecipe(match);
            }
        }
        if (preferences.isAlmostThereEnabled() && !almostMatches.isEmpty()) {
            adapter.addHeader(getString(R.string.section_almost, almostMatches.size()));
            for (MatchResult match : almostMatches) {
                adapter.addRecipe(match);
            }
        }

        boolean nothingToShow = adapter.getItemCount() == 0;
        binding.recyclerMatches.setVisibility(nothingToShow ? View.GONE : View.VISIBLE);
        binding.layoutEmptyState.setVisibility(nothingToShow ? View.VISIBLE : View.GONE);
        binding.textSuggestionSubtitle.setVisibility(nothingToShow ? View.GONE : View.VISIBLE);

        if (nothingToShow) {
            binding.textEmptyMessage.setText(buildEmptyMessage(pantry, almostMatches));
        }
    }

    //If nothing matches an explanation will be given
    private String buildEmptyMessage(List<PantryItem> pantry, List<MatchResult> almostMatches) {
        StringBuilder message = new StringBuilder();
        message.append(pantry.isEmpty()
                ? getString(R.string.empty_pantry_suggestions_message)
                : getString(R.string.empty_suggestions_message));
        if (!almostMatches.isEmpty()) {
            message.append("\n\n");
            message.append(getString(R.string.almost_hint, almostMatches.size()));
        }
        return message.toString();
    }
}
