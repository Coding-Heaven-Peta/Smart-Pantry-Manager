package za.co.richfield.smartpantry.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class IngredientNameNormalizerTest {

    @Test
    public void singularAndPluralNamesProduceTheSameKey() {
        assertEquals("tomato", IngredientNameNormalizer.normalize("tomato"));
        assertEquals("tomato", IngredientNameNormalizer.normalize("Tomatoes"));
        assertEquals("potato", IngredientNameNormalizer.normalize("POTATOES"));
        assertEquals("berry", IngredientNameNormalizer.normalize("berries"));
        assertEquals("egg", IngredientNameNormalizer.normalize("eggs"));
    }

    @Test
    public void fillerAndPreparationWordsAreIgnored() {
        assertEquals("tomato", IngredientNameNormalizer.normalize("Fresh Tomatoes"));
        assertEquals("onion", IngredientNameNormalizer.normalize("2 large chopped onions"));
        assertEquals("garlic", IngredientNameNormalizer.normalize("cloves of garlic"));
        assertEquals("flour", IngredientNameNormalizer.normalize("self-raising flour"));
    }

    @Test
    public void bracketedNotesAndPunctuationAreRemoved() {
        assertEquals("cheese", IngredientNameNormalizer.normalize("Cheese (grated)"));
        assertEquals("tomato", IngredientNameNormalizer.normalize("tomato,"));
    }

    @Test
    public void regionalSynonymsAreMapped() {
        assertEquals("eggplant", IngredientNameNormalizer.normalize("aubergine"));
        assertEquals("corn", IngredientNameNormalizer.normalize("mielies"));
        assertEquals("bell pepper", IngredientNameNormalizer.normalize("capsicum"));
    }

    @Test
    public void productChangingWordsAreKept() {
        // Tinned tomato is a different product from fresh tomato, so the keys differ.
        assertEquals("tinned tomato", IngredientNameNormalizer.normalize("tinned tomatoes"));
        assertEquals("tomato", IngredientNameNormalizer.normalize("tomato"));
    }

    @Test
    public void emptyInputIsHandledSafely() {
        assertEquals("", IngredientNameNormalizer.normalize(null));
        assertEquals("", IngredientNameNormalizer.normalize("   "));
    }
}
