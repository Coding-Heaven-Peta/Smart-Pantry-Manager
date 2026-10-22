package za.co.richfield.smartpantry.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public final class IngredientNameNormalizer {
    private static final Set<String> FILLER_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "of", "some", "any", "and", "or", "with", "for",
            "fresh", "ripe", "raw", "cooked", "chopped", "diced", "sliced", "minced",
            "grated", "peeled", "crushed", "melted", "softened", "beaten", "boiled",
            "roasted", "fried", "large", "medium", "small", "whole", "plain", "extra",
            "self", "raising", "cooking", "free", "range", "organic",
            "clove", "cloves", "sprig", "sprigs", "pinch", "pinches", "handful",
            "handfuls", "bunch", "bunches", "head", "heads", "stalk", "stalks",
            "stick", "sticks", "can", "cans", "tin", "tins", "packet", "packets",
            "sachet", "sachets", "punnet", "punnets"
    ));

    private static final Map<String, String> SYNONYMS;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("aubergine", "eggplant");
        map.put("brinjal", "eggplant");
        map.put("courgette", "zucchini");
        map.put("coriander", "cilantro");
        map.put("mealie", "corn");
        map.put("mielie", "corn");
        map.put("mealies", "corn");
        map.put("mielies", "corn");
        map.put("sweetcorn", "corn");
        map.put("chili", "chilli");
        map.put("chile", "chilli");
        map.put("chilly", "chilli");
        map.put("capsicum", "bell pepper");
        map.put("spring onion", "green onion");
        map.put("maize", "corn");
        SYNONYMS = Collections.unmodifiableMap(map);
    }

    private IngredientNameNormalizer() {
        // Utility class: no instances.
    }public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }

        // 1. Lower-case and remove bracketed notes such as "tomato (chopped)".
        String working = rawName.toLowerCase(Locale.ROOT).replaceAll("\\(.*?\\)", " ");
        // 2. Replace anything that is not a letter or a space with a space.
        working = working.replaceAll("[^a-z\\s]", " ");

        StringBuilder result = new StringBuilder();
        for (String token : working.trim().split("\\s+")) {
            if (token.isEmpty() || FILLER_WORDS.contains(token)) {
                continue;
            }
            String singular = singularize(token);
            // Check the synonym list against both the original and singular form,
            // so "mielies" and "mielie" both map to "corn".
            String synonym = SYNONYMS.get(token);
            if (synonym == null) {
                synonym = SYNONYMS.get(singular);
            }
            String canonical = synonym != null ? synonym : singular;
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(canonical);
        }
        return result.toString();
    }
    static String singularize(String word) {
        if (word.length() <= 2) {
            return word;
        }
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";   // berries -> berry
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);          // tomatoes -> tomato
        }
        if (word.endsWith("ses") || word.endsWith("xes") || word.endsWith("zes")
                || word.endsWith("ches") || word.endsWith("shes")) {
            return word.substring(0, word.length() - 2);          // dishes -> dish
        }
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;                                          // glass, citrus
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);          // eggs -> egg
        }
        return word;
    }
}
