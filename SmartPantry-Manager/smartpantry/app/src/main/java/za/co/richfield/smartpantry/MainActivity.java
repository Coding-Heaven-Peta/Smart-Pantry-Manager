package za.co.richfield.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import za.co.richfield.smartpantry.data.DatabaseHelper;
import za.co.richfield.smartpantry.databinding.ActivityMainBinding;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.ui.PantryAdapter;
import za.co.richfield.smartpantry.util.AppPreferences;
import za.co.richfield.smartpantry.util.DateUtils;

// Pantry List
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemListener {

    private static final int EXPIRING_SOON_DAYS = 3;

    private ActivityMainBinding binding;
    private DatabaseHelper databaseHelper;
    private AppPreferences preferences;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);
        preferences = new AppPreferences(this);

        adapter = new PantryAdapter(this);
        binding.recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerPantry.setAdapter(adapter);

        binding.fabAddItem.setOnClickListener(v -> openAddIngredientScreen());
        binding.buttonAddFirstItem.setOnClickListener(v -> openAddIngredientScreen());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_suggested) {
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                return true;
            }
            if (itemId == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return itemId == R.id.nav_pantry;
        });
        binding.bottomNavigation.setSelectedItemId(R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the user returns from another tab, put the selection back on "Pantry".
        binding.bottomNavigation.setSelectedItemId(R.id.nav_pantry);
        loadPantry();
    }

    // Pantry is saved in SQlite
    private void loadPantry() {
        List<PantryItem> items = databaseHelper.getAllPantryItems();

        adapter.setHighlightExpiry(preferences.isExpiryAlertsEnabled());
        adapter.submitItems(items);

        binding.textPantryCount.setText(getString(R.string.pantry_subtitle, items.size()));

        boolean isEmpty = items.isEmpty();
        binding.layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.fabAddItem.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        updateExpiryBanner(items);
    }

    //Checks items that are expiring and shows a warning to those near expiry date
    private void updateExpiryBanner(List<PantryItem> items) {
        if (!preferences.isExpiryAlertsEnabled()) {
            binding.cardExpiryBanner.setVisibility(View.GONE);
            return;
        }
        int expiring = 0;
        for (PantryItem item : items) {
            if (!item.hasExpiryDate()) {
                continue;
            }
            if (DateUtils.isExpired(item.getExpiryDate())
                    || DateUtils.isExpiringSoon(item.getExpiryDate(), EXPIRING_SOON_DAYS)) {
                expiring++;
            }
        }
        if (expiring > 0) {
            binding.cardExpiryBanner.setVisibility(View.VISIBLE);
            binding.textExpiryBanner.setText(getString(R.string.expiring_banner, expiring));
        } else {
            binding.cardExpiryBanner.setVisibility(View.GONE);
        }
    }

    private void openAddIngredientScreen() {
        startActivity(AddEditIngredientActivity.createIntent(this, AddEditIngredientActivity.NO_ID));
    }

    @Override
    public void onEditItem(PantryItem item) {
        startActivity(AddEditIngredientActivity.createIntent(this, item.getId()));
    }

    @Override
    public void onDeleteItem(final PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_item_title)
                .setMessage(getString(R.string.delete_item_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    databaseHelper.deletePantryItem(item.getId());
                    Toast.makeText(this, getString(R.string.item_deleted, item.getName()),
                            Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .show();
    }
}
