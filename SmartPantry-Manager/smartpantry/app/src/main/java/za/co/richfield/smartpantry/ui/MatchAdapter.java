package za.co.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import za.co.richfield.smartpantry.R;
import za.co.richfield.smartpantry.databinding.ItemRecipeBinding;
import za.co.richfield.smartpantry.databinding.ItemSectionHeaderBinding;
import za.co.richfield.smartpantry.model.IngredientCheck;
import za.co.richfield.smartpantry.model.MatchResult;
import za.co.richfield.smartpantry.model.Recipe;
public class MatchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_RECIPE = 1;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private static class Row {
        final String headerText;
        final MatchResult matchResult;

        private Row(String headerText, MatchResult matchResult) {
            this.headerText = headerText;
            this.matchResult = matchResult;
        }

        static Row header(String text) {
            return new Row(text, null);
        }

        static Row recipe(MatchResult result) {
            return new Row(null, result);
        }

        boolean isHeader() {
            return headerText != null;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public MatchAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void clear() {
        rows.clear();
        notifyDataSetChanged();
    }

    public void addHeader(String text) {
        rows.add(Row.header(text));
        notifyItemInserted(rows.size() - 1);
    }

    public void addRecipe(MatchResult result) {
        rows.add(Row.recipe(result));
        notifyItemInserted(rows.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_RECIPE;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            ItemSectionHeaderBinding binding =
                    ItemSectionHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(binding);
        }
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(inflater, parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(row.headerText);
        } else if (holder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) holder).bind(row.matchResult);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textSection;

        HeaderViewHolder(ItemSectionHeaderBinding binding) {
            super(binding.getRoot());
            this.textSection = binding.textSection;
        }

        void bind(String text) {
            textSection.setText(text);
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecipeBinding binding;

        RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final MatchResult result) {
            Recipe recipe = result.getRecipe();
            binding.textRecipeName.setText(recipe.getName());
            binding.textRecipeDescription.setText(recipe.getDescription());

            if (result.isFullMatch()) {
                binding.textRecipeStatus.setText(itemView.getContext().getString(
                        R.string.all_ingredients_available, recipe.getIngredients().size()));
                binding.textRecipeStatus.setTextColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.brand_green_dark));
            } else {
                IngredientCheck missing = result.getMissing().get(0);
                binding.textRecipeStatus.setText(itemView.getContext().getString(
                        R.string.missing_one, missing.getRequired().getName()));
                binding.textRecipeStatus.setTextColor(
                        ContextCompat.getColor(itemView.getContext(), R.color.accent_amber));
            }
            binding.textRecipeStatus.setVisibility(View.VISIBLE);

            binding.cardRoot.setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
}
