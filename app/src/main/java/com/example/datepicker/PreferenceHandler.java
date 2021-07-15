package com.example.datepicker;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceHandler {
    private static final String PREFERENCE_NAME = "dermopico";

    public static void setStrPreferences(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);

        prefEditor.commit();
    }

    public static void setBoolPreferences(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.commit();
    }

    public static void setIntPreferences(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, value);

        prefEditor.commit();
    }

    public static boolean getBoolPreferences(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public static int getIntPreferences(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, -1);
    }

    public static String getStrPreferences(Context context, String key, String def) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, def);
    }

    public static int getIntPreferences(Context context, String key, int def) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, def);
    }

    public static void setAllEmpty(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
    }

    public static void setEmpty(Context context, String pref_name) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.remove(pref_name);
        edit.commit();
    }

    public static String getStrPreferences(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, "");
    }
}
