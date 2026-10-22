package za.co.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.model.RecipeIngredient;


//SQLite persistence for the Smart Pantry Manager (SQLiteOpenHelper).

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "smartpantry.db";
    public static final int DATABASE_VERSION = 1;

    // pantry_items
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_SERVINGS = "servings";
    public static final String COL_STEPS = "steps";

    // recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_ING_ID = "_id";
    public static final String COL_ING_RECIPE_ID = "recipe_id";
    public static final String COL_ING_NAME = "name";
    public static final String COL_ING_QUANTITY = "quantity";
    public static final String COL_ING_UNIT = "unit";

    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL, "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + COL_EXPIRY + " TEXT)";

    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " ("
                    + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_NAME + " TEXT NOT NULL, "
                    + COL_DESCRIPTION + " TEXT, "
                    + COL_SERVINGS + " INTEGER NOT NULL DEFAULT 1, "
                    + COL_STEPS + " TEXT)";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                    + COL_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ING_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_ING_NAME + " TEXT NOT NULL, "
                    + COL_ING_QUANTITY + " REAL NOT NULL, "
                    + COL_ING_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + COL_ING_RECIPE_ID + ") REFERENCES "
                    + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)";

    private static volatile DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Required for ON DELETE CASCADE to work.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple migration strategy for this assignment: rebuild the schema and re-seed.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        List<Recipe> recipes = DatabaseSeeder.buildSeedRecipes();
        db.beginTransaction();
        try {
            for (Recipe recipe : recipes) {
                insertRecipe(db, recipe);
            }
            db.setTransactionSuccessful();
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to seed recipes", e);
        } finally {
            db.endTransaction();
        }
    }

    private long insertRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, recipe.getName());
        recipeValues.put(COL_DESCRIPTION, recipe.getDescription());
        recipeValues.put(COL_SERVINGS, recipe.getServings());
        recipeValues.put(COL_STEPS, recipe.getSteps());
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        if (recipeId == -1) {
            Log.e(TAG, "Could not insert recipe " + recipe.getName());
            return -1;
        }
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(COL_ING_RECIPE_ID, recipeId);
            ingredientValues.put(COL_ING_NAME, ingredient.getName());
            ingredientValues.put(COL_ING_QUANTITY, ingredient.getQuantity());
            ingredientValues.put(COL_ING_UNIT, ingredient.getUnit());
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
        }
        return recipeId;
    }

    //Pantry
    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, values);
        item.setId(id);
        return id;
    }

    public int insertPantryItems(List<PantryItem> items) {
        SQLiteDatabase db = getWritableDatabase();
        int inserted = 0;
        db.beginTransaction();
        try {
            for (PantryItem item : items) {
                ContentValues values = new ContentValues();
                values.put(COL_NAME, item.getName());
                values.put(COL_QUANTITY, item.getQuantity());
                values.put(COL_UNIT, item.getUnit());
                values.put(COL_EXPIRY, item.getExpiryDate());
                if (db.insert(TABLE_PANTRY, null, values) != -1) {
                    inserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return inserted;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor));
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return readPantryItem(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private PantryItem readPantryItem(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
        item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public int deleteAllPantryItems() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, null, null);
    }

    public int getPantryItemCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PANTRY, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Map<Long, Recipe> recipeById = new HashMap<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor recipeCursor = db.query(TABLE_RECIPES, null, null, null, null, null,
                COL_RECIPE_NAME + " COLLATE NOCASE ASC");
        try {
            while (recipeCursor.moveToNext()) {
                Recipe recipe = new Recipe();
                recipe.setId(recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
                recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
                recipe.setDescription(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
                recipe.setServings(recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(COL_SERVINGS)));
                recipe.setSteps(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_STEPS)));
                recipes.add(recipe);
                recipeById.put(recipe.getId(), recipe);
            }
        } finally {
            recipeCursor.close();
        }

        Cursor ingredientCursor = db.query(TABLE_RECIPE_INGREDIENTS, null, null, null,
                null, null, COL_ING_ID + " ASC");
        try {
            while (ingredientCursor.moveToNext()) {
                long recipeId = ingredientCursor.getLong(
                        ingredientCursor.getColumnIndexOrThrow(COL_ING_RECIPE_ID));
                Recipe recipe = recipeById.get(recipeId);
                if (recipe == null) {
                    continue;
                }
                recipe.addIngredient(new RecipeIngredient(
                        ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(COL_ING_NAME)),
                        ingredientCursor.getDouble(ingredientCursor.getColumnIndexOrThrow(COL_ING_QUANTITY)),
                        ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(COL_ING_UNIT))));
            }
        } finally {
            ingredientCursor.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(long recipeId) {
        for (Recipe recipe : getAllRecipes()) {
            if (recipe.getId() == recipeId) {
                return recipe;
            }
        }
        return null;
    }

    public int getRecipeCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }
    public boolean isPantryEmpty() {
        return getPantryItemCount() == 0;
    }
}
