package za.co.richfield.smartpantry;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import za.co.richfield.smartpantry.data.DatabaseHelper;
import za.co.richfield.smartpantry.data.DatabaseSeeder;
import za.co.richfield.smartpantry.databinding.ActivitySettingsBinding;
import za.co.richfield.smartpantry.util.AppPreferences;

// Settings
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private AppPreferences preferences;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new AppPreferences(this);
        databaseHelper = DatabaseHelper.getInstance(this);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Show the stored values without triggering the listeners.
        binding.switchExpiryAlerts.setChecked(preferences.isExpiryAlertsEnabled());
        binding.switchAlmostThere.setChecked(preferences.isAlmostThereEnabled());

        binding.switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> preferences.setExpiryAlertsEnabled(isChecked));
        binding.switchAlmostThere.setOnCheckedChangeListener(
                (buttonView, isChecked) -> preferences.setAlmostThereEnabled(isChecked));

        binding.buttonLoadDemo.setOnClickListener(v -> confirmLoadDemoPantry());
        binding.buttonClearPantry.setOnClickListener(v -> confirmClearPantry());
    }

    private void confirmLoadDemoPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.load_demo_pantry)
                .setMessage(R.string.confirm_load_demo)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.load_demo_pantry, (dialog, which) -> {
                    databaseHelper.deleteAllPantryItems();
                    int inserted = databaseHelper.insertPantryItems(DatabaseSeeder.buildDemoPantry());
                    Toast.makeText(this, getString(R.string.demo_loaded, inserted),
                            Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void confirmClearPantry() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_pantry)
                .setMessage(R.string.confirm_clear_pantry)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear_pantry, (dialog, which) -> {
                    databaseHelper.deleteAllPantryItems();
                    Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
