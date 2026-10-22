package za.co.richfield.smartpantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import za.co.richfield.smartpantry.data.DatabaseHelper;
import za.co.richfield.smartpantry.databinding.ActivityAddEditIngredientBinding;
import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.util.DateUtils;
// Add or edit ingredients in the pantry

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final long NO_ID = -1L;

    private static final String[] UNITS = {"g", "kg", "ml", "L", "piece"};

    private ActivityAddEditIngredientBinding binding;
    private DatabaseHelper databaseHelper;
    private boolean isEditMode;
    private long itemId = NO_ID;

    public static Intent createIntent(Context context, long itemId) {
        Intent intent = new Intent(context, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditIngredientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = DatabaseHelper.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, UNITS);
        binding.inputUnit.setAdapter(unitAdapter);

        binding.inputExpiry.setOnClickListener(v -> showDatePicker());
        binding.inputLayoutExpiry.setEndIconOnClickListener(v -> showDatePicker());
        binding.inputExpiry.setOnLongClickListener(v -> {
            binding.inputExpiry.setText("");
            Toast.makeText(this, R.string.no_expiry, Toast.LENGTH_SHORT).show();
            return true;
        });

        binding.buttonSave.setOnClickListener(v -> saveItem());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);
        isEditMode = itemId != NO_ID;
        if (isEditMode) {
            loadExistingItem();
        }
    }

    private void loadExistingItem() {
        PantryItem item = databaseHelper.getPantryItemById(itemId);
        if (item == null) {
            Toast.makeText(this, R.string.empty_pantry_title, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.toolbar.setTitle(R.string.title_edit_ingredient);
        binding.buttonSave.setText(R.string.update);
        binding.inputName.setText(item.getName());
        binding.inputQuantity.setText(String.valueOf(item.getQuantity()));
        binding.inputUnit.setText(item.getUnit(), false);
        if (item.hasExpiryDate()) {
            binding.inputExpiry.setText(item.getExpiryDate());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        Calendar existing = DateUtils.parseIso(textOf(binding.inputExpiry));
        if (existing != null) {
            calendar.setTimeInMillis(existing.getTimeInMillis());
        }
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, dayOfMonth);
                    binding.inputExpiry.setText(DateUtils.toIso(chosen));
                    binding.inputLayoutExpiry.setError(null);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(R.string.pick_expiry_date);
        dialog.show();
    }

    // field validation
    private boolean validateForm() {
        boolean valid = true;

        String name = textOf(binding.inputName).trim();
        if (TextUtils.isEmpty(name)) {
            binding.inputLayoutName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2) {
            binding.inputLayoutName.setError(getString(R.string.error_name_too_short));
            valid = false;
        } else {
            binding.inputLayoutName.setError(null);
        }

        String quantityText = textOf(binding.inputQuantity).trim();
        double quantity = -1;
        if (TextUtils.isEmpty(quantityText)) {
            binding.inputLayoutQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                // Accept a comma as a decimal separator
                quantity = Double.parseDouble(quantityText.replace(',', '.'));
                if (quantity <= 0) {
                    binding.inputLayoutQuantity.setError(getString(R.string.error_quantity_invalid));
                    valid = false;
                } else {
                    binding.inputLayoutQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                binding.inputLayoutQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        if (TextUtils.isEmpty(textOf(binding.inputUnit).trim())) {
            binding.inputLayoutUnit.setError(getString(R.string.error_unit_required));
            valid = false;
        } else {
            binding.inputLayoutUnit.setError(null);
        }

        String expiry = textOf(binding.inputExpiry).trim();
        if (!TextUtils.isEmpty(expiry) && !DateUtils.isValidFutureOrToday(expiry)) {
            binding.inputLayoutExpiry.setError(getString(R.string.error_expiry_past));
            valid = false;
        } else {
            binding.inputLayoutExpiry.setError(null);
        }

        return valid;
    }

    private void saveItem() {
        if (!validateForm()) {
            return;
        }

        String name = textOf(binding.inputName).trim();
        double quantity = Double.parseDouble(textOf(binding.inputQuantity).trim().replace(',', '.'));
        String unit = textOf(binding.inputUnit).trim();
        String expiry = textOf(binding.inputExpiry).trim();

        PantryItem item = new PantryItem(itemId, name, quantity, unit, expiry);

        if (isEditMode) {
            databaseHelper.updatePantryItem(item);
            Toast.makeText(this, R.string.saved_changes, Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.insertPantryItem(item);
            Toast.makeText(this, getString(R.string.saved_new_item, name), Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }

    private String textOf(android.widget.TextView view) {
        return view.getText() == null ? "" : view.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
