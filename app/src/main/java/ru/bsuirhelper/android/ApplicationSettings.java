package ru.bsuirhelper.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Влад on 17.10.13.
 */
public class ApplicationSettings {
    private static ApplicationSettings instance;
    private static SharedPreferences settings;
    private static final String PREFS_NAME = "settings.txt";
    public static final String DEFAULT_GROUP_OF_SCHEDULE = "defaultgroup";

    private ApplicationSettings(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, 1);
    }

    public static synchronized ApplicationSettings getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationSettings(context);
        }
        return instance;
    }

    public int getInt(String varName, int defaultValue) {
        return settings.getInt(varName, defaultValue);
    }

    public String getString(String varName, String defaultValue) {
        return settings.getString(varName, defaultValue);
    }

    public boolean getBoolean(String varName, boolean defaultValue) {
        return settings.getBoolean(varName, defaultValue);
    }

    public boolean putInt(String varName, int value) {
        return settings.edit().putInt(varName, value).commit();
    }

    public boolean putString(String varName, String value) {
        return settings.edit().putString(varName, value).commit();
    }

    public boolean putBoolean(String varName, boolean value) {
        return settings.edit().putBoolean(varName, value).commit();
    }

    public String getDefaultGroupOfSchedule() {
        return getString(DEFAULT_GROUP_OF_SCHEDULE, null);
    }
}
