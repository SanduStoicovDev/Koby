package com.unimib.koby.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/**
 * Singleton che gestisce preferenze semplici: tema scuro e lingua.
 */
public class SettingsManager {

    private static final String KEY_THEME   = "pref_dark_theme";
    private static final String KEY_LANG   = "pref_lang_en"; // true = EN, false = IT

    private static SettingsManager instance;
    private final SharedPreferences prefs;

    private SettingsManager(Context ctx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    public static synchronized SettingsManager getInstance(Context ctx) {
        if (instance == null) instance = new SettingsManager(ctx);
        return instance;
    }

    public boolean isDarkTheme() { return prefs.getBoolean(KEY_THEME, false); }
    public void setDarkTheme(boolean dark) { prefs.edit().putBoolean(KEY_THEME, dark).apply(); }

    public boolean isEnglish() { return prefs.getBoolean(KEY_LANG, false); }
    public void setEnglish(boolean en) { prefs.edit().putBoolean(KEY_LANG, en).apply(); }
}