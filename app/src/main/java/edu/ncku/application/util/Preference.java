package edu.ncku.application.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 本機端資料管理類別
 */
public class Preference {

    private static final String DEBUG_FLAG = Preference.class.getName();

    /**
     * 確認手機是否已訂閱推播訊息(跟伺服器可能會不一致)
     *
     * @param context
     * @param notifyUsername
     * @return
     */
    public static boolean isSub(Context context, String notifyUsername){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        String username = SP.getString(PreferenceKeys.ACCOUNT, "");
        boolean sub = SP.getBoolean(PreferenceKeys.SUBSCRIPTION, false);

        if (username.isEmpty() || !username.equals(notifyUsername)) { // 如果沒登入或者帳號與notifyUsername不一致則一律回傳false
            return false;
        } else {
            return sub;
        }
    }

    /**
     * 取得使用者姓名
     *
     * @param context
     * @return
     */
    public static String getName(Context context){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.NAME, "");
    }

    /**
     * 取得使用者帳號
     *
     * @param context
     * @return
     */
    public static String getUsername(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.ACCOUNT, "");
    }

    /**
     * 取得GCM Device ID
     *
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.DEVICE_TOKEN, "");
    }

    /**
     * 取得備份在館人數
     *
     * @param context
     * @return
     */
    public static String getVisitor(Context context) {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        return SP.getString(PreferenceKeys.VISITOR, "");
    }

    /**
     * 儲存使用者姓名
     *
     * @param context
     * @param name
     */
    public static void setName(Context context, String name) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.NAME, name).apply();
    }

    /**
     * 儲存帳號，同時表示已登入
     *
     * @param context
     * @param username
     */
    public static void setUsername(Context context, String username) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.ACCOUNT, username).apply();
    }

    /**
     * 儲存訂閱狀態
     *
     * @param context
     * @param sub
     */
    public static void setSubscription(Context context, boolean sub) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, sub).apply();
    }

    /**
     * 儲存在館人數
     *
     * @param context
     * @param visitor
     */
    public static void setVisitor(Context context, String visitor) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PreferenceKeys.VISITOR, visitor).apply();
    }

    /**
     * 確認是否已登入(以判斷帳號是否存在為依據)
     *
     * @param context
     * @return
     */
    public static boolean isLoggin(Context context) {
        String username = getUsername(context);
        return username != null && !username.isEmpty();
    }
}
