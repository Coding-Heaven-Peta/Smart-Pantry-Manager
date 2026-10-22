package za.co.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import za.co.richfield.smartpantry.R;
import za.co.richfield.smartpantry.databinding.ItemPantryBinding;
import za.co.richfield.smartpantry.logic.UnitConverter;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.util.DateUtils;


public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private static final int EXPIRING_SOON_DAYS = 3;

    public interface OnPantryItemListener {
        void onEditItem(PantryItem item);

        void onDeleteItem(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemListener listener;
    private boolean highlightExpiry = true;

    public PantryAdapter(OnPantryItemListener listener) {
        this.listener = listener;
    }

    public void submitItems(List<PantryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setHighlightExpiry(boolean highlightExpiry) {
        this.highlightExpiry = highlightExpiry;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPantryBinding binding = ItemPantryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PantryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PantryViewHolder extends RecyclerView.ViewHolder {

        private final ItemPantryBinding binding;

        PantryViewHolder(ItemPantryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final PantryItem item) {
            binding.textName.setText(item.getName());

            // Show quantities in the friendliest unit: 1000 g is displayed as 1 kg.
            double baseQuantity = UnitConverter.toBaseUnit(item.getQuantity(), item.getUnit());
            binding.textQuantity.setText(UnitConverter.formatQuantity(baseQuantity, item.getUnit()));

            bindExpiryBadge(item);

            binding.cardRoot.setOnClickListener(v -> listener.onEditItem(item));
            binding.buttonDelete.setOnClickListener(v -> listener.onDeleteItem(item));
        }

        private void bindExpiryBadge(PantryItem item) {
            if (!item.hasExpiryDate()) {
                binding.textExpiry.setText(R.string.no_expiry);
                binding.textExpiry.setBackgroundResource(R.drawable.bg_badge_normal);
                binding.textExpiry.setTextColor(color(R.color.text_secondary));
                return;
            }

            binding.textExpiry.setText(DateUtils.describeExpiry(item.getExpiryDate()));

            if (!highlightExpiry) {
                binding.textExpiry.setBackgroundResource(R.drawable.bg_badge_normal);
                binding.textExpiry.setTextColor(color(R.color.brand_green_dark));
                return;
            }

            if (DateUtils.isExpired(item.getExpiryDate())) {
                binding.textExpiry.setBackgroundResource(R.drawable.bg_badge_expired);
                binding.textExpiry.setTextColor(color(R.color.danger_red));
            } else if (DateUtils.isExpiringSoon(item.getExpiryDate(), EXPIRING_SOON_DAYS)) {
                binding.textExpiry.setBackgroundResource(R.drawable.bg_badge_warning);
                binding.textExpiry.setTextColor(color(android.R.color.holo_orange_dark));
            } else {
                binding.textExpiry.setBackgroundResource(R.drawable.bg_badge_normal);
                binding.textExpiry.setTextColor(color(R.color.brand_green_dark));
            }
        }

        private int color(int colorResource) {
            return ContextCompat.getColor(itemView.getContext(), colorResource);
        }
    }
}
