package za.co.richfield.smartpantry.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class UnitConverterTest {

    @Test
    public void massUnitsConvertToGrams() {
        assertEquals(500.0, UnitConverter.toBaseUnit(500, "g"), 0.001);
        assertEquals(500.0, UnitConverter.toBaseUnit(0.5, "kg"), 0.001);
        assertEquals(1.0, UnitConverter.toBaseUnit(1000, "mg"), 0.001);
    }

    @Test
    public void volumeUnitsConvertToMillilitres() {
        assertEquals(1000.0, UnitConverter.toBaseUnit(1, "L"), 0.001);
        assertEquals(1000.0, UnitConverter.toBaseUnit(100, "cl"), 0.001);
        assertEquals(250.0, UnitConverter.toBaseUnit(1, "cup"), 0.001);
    }

    @Test
    public void pluralAndUpperCasedUnitsAreAccepted() {
        assertEquals(UnitConverter.Family.MASS, UnitConverter.familyOf("Grams"));
        assertEquals(UnitConverter.Family.MASS, UnitConverter.familyOf("KG"));
        assertEquals(UnitConverter.Family.COUNT, UnitConverter.familyOf("pieces"));
    }

    @Test
    public void massAndVolumeAreNotCompatible() {
        assertTrue(UnitConverter.areCompatible("g", "kg"));
        assertTrue(UnitConverter.areCompatible("ml", "L"));
        assertFalse(UnitConverter.areCompatible("g", "ml"));
        assertFalse(UnitConverter.areCompatible("piece", "g"));
    }

    @Test
    public void quantitiesAreFormattedForHumans() {
        assertEquals("1.5 kg", UnitConverter.formatQuantity(1500, "g"));
        assertEquals("500 g", UnitConverter.formatQuantity(500, "g"));
        assertEquals("1 L", UnitConverter.formatQuantity(1000, "ml"));
        assertEquals("3 pieces", UnitConverter.formatQuantity(3, "piece"));
        assertEquals("1 piece", UnitConverter.formatQuantity(1, "piece"));
    }
}
