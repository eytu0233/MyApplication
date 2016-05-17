package edu.ncku.application.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

        Log.d(DEBUG_FLAG, "username : " + username);
        Log.d(DEBUG_FLAG, "password : " + password);

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static String getLoginName(Context context){
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

}
