package com.yehancha.jay.ncure;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    public static final String SERVER_URL = "http://212.47.230.4/n-cure/";

    private static final String PREF_KEY_USER_ID = "user_id";

    public static void setUserId(Context context, String userId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_USER_ID, userId);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_KEY_USER_ID, null);
    }

    public static boolean isValidString(String s) {
        return s != null && !s.isEmpty() && !s.trim().isEmpty();
    }
}
