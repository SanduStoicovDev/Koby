package com.unimib.koby.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to read and write data using SharedPreferences API.
 * Doc can be read here: <a href="https://developer.android.com/training/data-storage/shared-preferences">...</a>
 */
public class SharedPreferencesUtil {

    private final Application application;

    public SharedPreferencesUtil(Application application) {
        this.application = application;
    }

    /**
     * Writes a String value using SharedPreferences API.
     * @param sharedPreferencesFileName The name of file where to write data.
     * @param key The key associated with the value to write.
     * @param value The value to write associated with the key.
     */
    public void writeStringData(String sharedPreferencesFileName, String key, String value) {
        SharedPreferences sharedPref = application.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Returns the String value associated with the key passed as argument
     * using SharedPreferences API.
     * @param sharedPreferencesFileName The name of file where to read the data.
     * @param key The key associated with the value to read.
     * @return The String value associated with the key passed as argument.
     */
    public String readStringData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = application.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    /**
     * Returns the boolean value associated with the key passed as argument
     * using SharedPreferences API.
     * @param sharedPreferencesFileName The name of file where to read the data.
     * @param key The key associated with the value to read.
     * @return The boolean value associated with the key passed as argument.
     */
    public boolean readBooleanData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = application.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    /**
     * Writes a boolean value using SharedPreferences API.
     * @param sharedPreferencesFileName The name of file where to write data.
     * @param key The key associated with the value to write.
     * @param value The value to write associated with the key.
     */
    public void writeBooleanData(String sharedPreferencesFileName, String key, boolean value) {
        SharedPreferences sharedPref = application.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
