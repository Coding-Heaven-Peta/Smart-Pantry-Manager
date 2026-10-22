package za.co.richfield.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_ALMOST_THERE = "almost_there_enabled";

    private final SharedPreferences preferences;

    public AppPreferences(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return preferences.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    public boolean isAlmostThereEnabled() {
        return preferences.getBoolean(KEY_ALMOST_THERE, true);
    }

    public void setAlmostThereEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_ALMOST_THERE, enabled).apply();
    }
}
