package edu.ncku.application.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by NCKU on 2016/5/17.
 */
public class Preference {

    private static final String DEBUG_FLAG = Preference.class.getName();

    public static boolean isLoggin(Context context){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        String username = SP.getString(PreferenceKeys.USERNAME, ""), password = SP.getString(PreferenceKeys.PASSWORD,
                "");

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isSub(Context context, String notifyUsername){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        String username = SP.getString(PreferenceKeys.USERNAME, ""),
                password = SP.getString(PreferenceKeys.PASSWORD, "");
        boolean sub = SP.getBoolean(PreferenceKeys.SUBSCRIPTION, true);

        if (username.isEmpty() || password.isEmpty() || !username.equals(notifyUsername)) {
            return false;
        } else {
            return sub;
        }
    }

    public static String getName(Context context){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.NAME, "");
    }

    public static String getUsername(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.USERNAME, "");
    }

    public static String getDeviceID(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.DEVICE_TOKEN, "");
    }

    public static String getVisitor(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.VISITOR, "");
    }

    public static void setName(Context context, String name) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.NAME, name).apply();
    }

    public static void setUsername(Context context, String username) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.USERNAME, username).apply();
    }

    public static void setPassword(Context context, String password) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.PASSWORD, password).apply();
    }

    public static void setSubscription(Context context, boolean sub) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, sub).apply();
    }

    public static void setVisitor(Context context, String visitor) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.VISITOR, visitor).apply();
    }
}
