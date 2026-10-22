package za.co.richfield.smartpantry.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UnitConverter {

    public enum Family { MASS, VOLUME, COUNT, UNKNOWN }

    private static final Map<String, Double> MASS_FACTORS;
    private static final Map<String, Double> VOLUME_FACTORS;
    private static final Map<String, Double> COUNT_FACTORS;

    static {
        Map<String, Double> mass = new HashMap<>();
        mass.put("mg", 0.001);
        mass.put("g", 1.0);
        mass.put("gram", 1.0);
        mass.put("gramme", 1.0);
        mass.put("kg", 1000.0);
        mass.put("kilogram", 1000.0);
        mass.put("kilo", 1000.0);
        MASS_FACTORS = Collections.unmodifiableMap(mass);

        Map<String, Double> volume = new HashMap<>();
        volume.put("ml", 1.0);
        volume.put("millilitre", 1.0);
        volume.put("milliliter", 1.0);
        volume.put("cl", 10.0);
        volume.put("l", 1000.0);
        volume.put("litre", 1000.0);
        volume.put("liter", 1000.0);
        volume.put("lt", 1000.0);
        volume.put("cup", 250.0);
        volume.put("tbsp", 15.0);
        volume.put("tablespoon", 15.0);
        volume.put("tsp", 5.0);
        volume.put("teaspoon", 5.0);
        VOLUME_FACTORS = Collections.unmodifiableMap(volume);

        Map<String, Double> count = new HashMap<>();
        for (String unit : new String[]{"piece", "pc", "each", "unit", "item", "whole",
                "clove", "slice", "tin", "can", "bunch", "head", "sachet", "packet"}) {
            count.put(unit, 1.0);
        }
        COUNT_FACTORS = Collections.unmodifiableMap(count);
    }

    private UnitConverter() {
        // Utility class: no instances.
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String normalised = unit.trim().toLowerCase(Locale.ROOT);
        if (normalised.length() > 2 && normalised.endsWith("s")) {
            normalised = normalised.substring(0, normalised.length() - 1);
        }
        return normalised;
    }

    public static Family familyOf(String unit) {
        String key = normalizeUnit(unit);
        if (MASS_FACTORS.containsKey(key)) {
            return Family.MASS;
        }
        if (VOLUME_FACTORS.containsKey(key)) {
            return Family.VOLUME;
        }
        if (COUNT_FACTORS.containsKey(key)) {
            return Family.COUNT;
        }
        return Family.UNKNOWN;
    }


    public static double toBaseUnit(double quantity, String unit) {
        String key = normalizeUnit(unit);
        Double factor = MASS_FACTORS.get(key);
        if (factor == null) {
            factor = VOLUME_FACTORS.get(key);
        }
        if (factor == null) {
            factor = COUNT_FACTORS.get(key);
        }
        if (factor == null) {
            return quantity;
        }
        return quantity * factor;
    }


    public static boolean areCompatible(String unitA, String unitB) {
        Family familyA = familyOf(unitA);
        Family familyB = familyOf(unitB);
        if (familyA == Family.UNKNOWN || familyB == Family.UNKNOWN) {
            return normalizeUnit(unitA).equals(normalizeUnit(unitB));
        }
        return familyA == familyB;
    }


    public static String formatQuantity(double baseQuantity, String unitHint) {
        Family family = familyOf(unitHint);
        switch (family) {
            case MASS:
                if (baseQuantity >= 1000) {
                    return formatNumber(baseQuantity / 1000) + " kg";
                }
                return formatNumber(baseQuantity) + " g";
            case VOLUME:
                if (baseQuantity >= 1000) {
                    return formatNumber(baseQuantity / 1000) + " L";
                }
                return formatNumber(baseQuantity) + " ml";
            case COUNT:
                return formatNumber(baseQuantity) + (baseQuantity == 1 ? " piece" : " pieces");
            default:
                return formatNumber(baseQuantity) + " " + (unitHint == null ? "" : unitHint.trim());
        }
    }

    public static String formatNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001) {
            return String.valueOf((long) Math.rint(value));
        }
        String text = String.format(Locale.US, "%.2f", value);
        while (text.contains(".") && (text.endsWith("0") || text.endsWith("."))) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}
