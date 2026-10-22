package za.co.richfield.smartpantry.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.co.richfield.smartpantry.model.PantryItem;
import za.co.richfield.smartpantry.model.Recipe;
import za.co.richfield.smartpantry.model.RecipeIngredient;
import za.co.richfield.smartpantry.util.DateUtils;

/**
 * Static seed data for the app.
 *
 * <p>The recipe collection is written on first run (see DatabaseHelper.onCreate) so the
 * user always has recipes to match against. The pantry itself is NOT seeded, because the
 * empty-pantry state is part of the required behaviour; instead the Settings screen offers
 * a "Load demo pantry" button that a demonstrator can use to populate a realistic pantry.</p>
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {
        // Utility class: no instances.
    }

    // ---------------------------------------------------------------------
    // Pantry demo data
    // ---------------------------------------------------------------------

    /**
     * A realistic pantry that shows every behaviour of the app:
     * 8 recipes can be cooked, several are "almost there", and a few items
     * are close to their expiry date.
     */
    public static List<PantryItem> buildDemoPantry() {
        List<PantryItem> items = new ArrayList<>();
        items.add(new PantryItem("Egg", 6, "piece", DateUtils.isoDaysFromToday(12)));
        items.add(new PantryItem("Bread", 8, "piece", DateUtils.isoDaysFromToday(3)));
        items.add(new PantryItem("Cheese", 200, "g", DateUtils.isoDaysFromToday(14)));
        items.add(new PantryItem("Tomato", 3, "piece", DateUtils.isoDaysFromToday(4)));
        items.add(new PantryItem("Onion", 2, "piece", ""));
        items.add(new PantryItem("Garlic", 4, "piece", ""));
        items.add(new PantryItem("Rice", 500, "g", ""));
        items.add(new PantryItem("Pasta", 500, "g", ""));
        items.add(new PantryItem("Milk", 1, "L", DateUtils.isoDaysFromToday(2)));
        items.add(new PantryItem("Butter", 250, "g", DateUtils.isoDaysFromToday(20)));
        items.add(new PantryItem("Flour", 1, "kg", ""));
        items.add(new PantryItem("Cooking oil", 750, "ml", ""));
        items.add(new PantryItem("Salt", 500, "g", ""));
        items.add(new PantryItem("Sugar", 1, "kg", ""));
        return items;
    }

    // ---------------------------------------------------------------------
    // Recipe collection (20 recipes)
    // ---------------------------------------------------------------------

    public static List<Recipe> buildSeedRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(recipe("Cheese Omelette",
                "A quick three-minute breakfast using eggs and whatever cheese is left.",
                1,
                "Beat the eggs with a pinch of salt until smooth.\n"
                        + "Melt the butter in a non-stick pan over medium heat.\n"
                        + "Pour in the eggs and cook without stirring for two minutes.\n"
                        + "Sprinkle the cheese over one half, fold the omelette and serve.",
                ing("Egg", 2, "piece"), ing("Cheese", 50, "g"),
                ing("Butter", 10, "g"), ing("Salt", 1, "g")));

        recipes.add(recipe("Scrambled Eggs on Toast",
                "Creamy scrambled eggs on buttered toast - the classic student breakfast.",
                2,
                "Whisk the eggs with the milk and salt.\n"
                        + "Melt the butter in a pan over low heat and add the egg mixture.\n"
                        + "Stir slowly until just set, then take the pan off the heat.\n"
                        + "Toast the bread, butter it lightly and spoon the eggs on top.",
                ing("Egg", 3, "piece"), ing("Bread", 2, "piece"),
                ing("Butter", 15, "g"), ing("Milk", 30, "ml"), ing("Salt", 1, "g")));

        recipes.add(recipe("Grilled Cheese Sandwich",
                "Crisp toasted bread with melted cheese in the middle.",
                1,
                "Butter one side of each slice of bread.\n"
                        + "Put the cheese between the unbuttered sides.\n"
                        + "Fry in a dry pan over medium heat until golden on both sides.",
                ing("Bread", 2, "piece"), ing("Cheese", 60, "g"), ing("Butter", 20, "g")));

        recipes.add(recipe("Garlic Butter Pasta",
                "Pasta tossed in garlic butter - five ingredients, ten minutes.",
                2,
                "Boil the pasta in salted water until just tender and keep a little water.\n"
                        + "Melt the butter and fry the crushed garlic for one minute.\n"
                        + "Add the drained pasta and a splash of pasta water, toss and season.",
                ing("Pasta", 200, "g"), ing("Butter", 40, "g"),
                ing("Garlic", 3, "piece"), ing("Salt", 2, "g")));

        recipes.add(recipe("White Sauce Pasta",
                "A basic white sauce turned into a filling pasta dish.",
                2,
                "Boil the pasta until tender and drain.\n"
                        + "Melt the butter, stir in the flour and cook for one minute.\n"
                        + "Add the milk a little at a time, stirring until the sauce thickens.\n"
                        + "Stir in the cheese and salt, then fold through the pasta.",
                ing("Pasta", 250, "g"), ing("Flour", 30, "g"), ing("Milk", 400, "ml"),
                ing("Butter", 40, "g"), ing("Cheese", 50, "g"), ing("Salt", 2, "g")));

        recipes.add(recipe("Mac and Cheese",
                "Baked macaroni in a thick cheese sauce with a golden top.",
                3,
                "Cook the pasta until just underdone and drain.\n"
                        + "Make a roux with the butter and flour, then whisk in the milk.\n"
                        + "Stir in the cheese until melted and season with salt.\n"
                        + "Mix with the pasta and bake at 200 C for 15 minutes.",
                ing("Pasta", 250, "g"), ing("Milk", 300, "ml"), ing("Butter", 30, "g"),
                ing("Flour", 25, "g"), ing("Cheese", 150, "g"), ing("Salt", 2, "g")));

        recipes.add(recipe("Cheesy Garlic Bread",
                "Garlic butter and cheese grilled onto bread - a quick side dish.",
                3,
                "Mash the butter with crushed garlic.\n"
                        + "Spread over the bread slices and top with grated cheese.\n"
                        + "Grill until the cheese bubbles and the edges are crisp.",
                ing("Bread", 4, "piece"), ing("Butter", 40, "g"),
                ing("Garlic", 3, "piece"), ing("Cheese", 100, "g")));

        recipes.add(recipe("Fluffy Pancakes",
                "Thick, fluffy pancakes made from pantry basics.",
                3,
                "Mix the flour, sugar, baking powder and salt.\n"
                        + "Whisk in the milk and eggs until just combined - do not overmix.\n"
                        + "Fry spoonfuls in a hot, lightly greased pan until bubbles form.\n"
                        + "Flip and cook for another minute.",
                ing("Flour", 250, "g"), ing("Milk", 300, "ml"), ing("Egg", 2, "piece"),
                ing("Sugar", 30, "g"), ing("Baking powder", 10, "g"), ing("Salt", 2, "g")));

        recipes.add(recipe("French Toast",
                "Stale bread dipped in egg and milk, fried in butter with cinnamon.",
                2,
                "Beat the eggs with the milk, sugar and cinnamon.\n"
                        + "Soak each slice of bread for a few seconds on both sides.\n"
                        + "Fry in butter over medium heat until golden, then serve warm.",
                ing("Bread", 4, "piece"), ing("Egg", 2, "piece"), ing("Milk", 100, "ml"),
                ing("Sugar", 15, "g"), ing("Butter", 15, "g"), ing("Cinnamon", 2, "g")));

        recipes.add(recipe("Tomato Pasta",
                "A simple onion and tomato pasta sauce made from tinned tomatoes.",
                3,
                "Fry the chopped onion and garlic in the oil until soft.\n"
                        + "Add the tinned tomato and simmer for 15 minutes.\n"
                        + "Season with salt and stir through cooked pasta.",
                ing("Pasta", 250, "g"), ing("Tinned tomato", 400, "g"), ing("Onion", 1, "piece"),
                ing("Garlic", 2, "piece"), ing("Cooking oil", 15, "ml"), ing("Salt", 2, "g")));

        recipes.add(recipe("Chicken and Rice",
                "Pan-fried chicken with fluffy rice and a garlicky onion base.",
                2,
                "Fry the chicken in the oil until cooked through, then set aside.\n"
                        + "Soften the onion and garlic in the same pan.\n"
                        + "Add the rice, water and salt, cover and simmer for 20 minutes.\n"
                        + "Serve the chicken on top of the rice.",
                ing("Rice", 250, "g"), ing("Chicken breast", 300, "g"), ing("Onion", 1, "piece"),
                ing("Garlic", 2, "piece"), ing("Cooking oil", 15, "ml"), ing("Salt", 2, "g")));

        recipes.add(recipe("Vegetable Fried Rice",
                "Leftover rice fried with egg, carrot and peas.",
                2,
                "Heat the oil and scramble the eggs, then push them to one side.\n"
                        + "Fry the diced carrot and peas for three minutes.\n"
                        + "Add the cooked rice and soy sauce and stir-fry until hot.",
                ing("Rice", 300, "g"), ing("Egg", 2, "piece"), ing("Carrot", 1, "piece"),
                ing("Peas", 100, "g"), ing("Soy sauce", 30, "ml"), ing("Cooking oil", 15, "ml")));

        recipes.add(recipe("Vegetarian Lentil Curry",
                "A cheap, filling lentil curry made creamy with coconut milk.",
                3,
                "Fry the onion and garlic in the oil until golden.\n"
                        + "Add the curry powder and cook for 30 seconds.\n"
                        + "Add the lentils and coconut milk, then simmer for 25 minutes.",
                ing("Lentils", 200, "g"), ing("Coconut milk", 400, "ml"), ing("Onion", 1, "piece"),
                ing("Garlic", 2, "piece"), ing("Curry powder", 15, "g"), ing("Cooking oil", 15, "ml")));

        recipes.add(recipe("Beef Mince Pasta",
                "A quick bolognese-style mince sauce with tinned tomatoes.",
                3,
                "Brown the mince in the oil, then add the onion and garlic.\n"
                        + "Stir in the tinned tomato and simmer for 20 minutes.\n"
                        + "Season and serve over cooked pasta.",
                ing("Pasta", 250, "g"), ing("Beef mince", 400, "g"), ing("Tinned tomato", 400, "g"),
                ing("Onion", 1, "piece"), ing("Garlic", 2, "piece"), ing("Cooking oil", 15, "ml")));

        recipes.add(recipe("Tuna Mayo Toast",
                "Cold tuna mayo on hot toast, with cucumber for crunch.",
                1,
                "Mix the drained tuna with the mayonnaise.\n"
                        + "Toast the bread and slice the cucumber.\n"
                        + "Pile the tuna on the toast and top with cucumber.",
                ing("Bread", 2, "piece"), ing("Tuna", 170, "g"),
                ing("Mayonnaise", 30, "g"), ing("Cucumber", 1, "piece")));

        recipes.add(recipe("Banana Oat Smoothie",
                "A breakfast smoothie that uses up ripe bananas and oats.",
                2,
                "Put the oats, banana, milk and yoghurt in a blender.\n"
                        + "Add the honey and blend until smooth.\n"
                        + "Serve immediately over ice.",
                ing("Oats", 50, "g"), ing("Banana", 1, "piece"), ing("Milk", 250, "ml"),
                ing("Honey", 15, "g"), ing("Yoghurt", 100, "g")));

        recipes.add(recipe("Chocolate Mug Cake",
                "A single-serving chocolate cake cooked in the microwave.",
                1,
                "Mix the flour, sugar, cocoa and baking powder in a mug.\n"
                        + "Stir in the milk and melted butter until smooth.\n"
                        + "Microwave on high for 70 seconds and allow to stand for one minute.",
                ing("Flour", 60, "g"), ing("Sugar", 40, "g"), ing("Cocoa", 20, "g"),
                ing("Milk", 60, "ml"), ing("Butter", 20, "g"), ing("Baking powder", 2, "g")));

        recipes.add(recipe("Butternut Soup",
                "A smooth, warming soup for cold evenings.",
                4,
                "Fry the chopped onion in the butter until soft.\n"
                        + "Add the diced butternut and stock, then simmer for 25 minutes.\n"
                        + "Blend until smooth, stir in the milk and season to taste.",
                ing("Butternut", 500, "g"), ing("Onion", 1, "piece"),
                ing("Stock cube", 1, "piece"), ing("Milk", 100, "ml"), ing("Butter", 20, "g")));

        recipes.add(recipe("Tomato and Onion Salad",
                "A fresh three-minute salad that uses up ripe tomatoes.",
                2,
                "Slice the tomatoes, onion and cucumber thinly.\n"
                        + "Whisk the oil, vinegar and salt into a dressing.\n"
                        + "Pour over the vegetables and serve.",
                ing("Tomato", 3, "piece"), ing("Onion", 1, "piece"), ing("Cucumber", 1, "piece"),
                ing("Vinegar", 15, "ml"), ing("Cooking oil", 15, "ml"), ing("Salt", 2, "g")));

        recipes.add(recipe("Cheese and Tomato Toastie",
                "A toasted sandwich with tomato and melted cheese.",
                1,
                "Butter the bread and layer cheese and sliced tomato inside.\n"
                        + "Close the sandwich and press it together.\n"
                        + "Toast in a pan over medium heat until golden and the cheese melts.",
                ing("Bread", 2, "piece"), ing("Cheese", 50, "g"),
                ing("Tomato", 2, "piece"), ing("Butter", 15, "g")));

        return recipes;
    }

    // ---------------------------------------------------------------------
    // Small builders to keep the recipe data readable
    // ---------------------------------------------------------------------

    private static Recipe recipe(String name, String description, int servings,
                                 String steps, RecipeIngredient... ingredients) {
        return new Recipe(name, description, servings, steps, Arrays.asList(ingredients));
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }
}
