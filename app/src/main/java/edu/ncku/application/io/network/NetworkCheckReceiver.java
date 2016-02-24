package edu.ncku.application.io.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import edu.ncku.application.util.PreferenceKeys;

/**
 * 此Receiver類別由NetworkListenerService註冊，接收網路狀態改變的事件
 */
public class NetworkCheckReceiver extends BroadcastReceiver {

    private static final String DEBUG_FLAG = NetworkCheckReceiver.class
            .getName();

    private NetworkInfo currentNetworkInfo;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

    public NetworkCheckReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {

            if (context != null) {
                ConnectivityManager connectivityManager = ((ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE));
                currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }

            /* 當連上網路時，在背景執行資料更新的工作 */
            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                // do something when network connected.
                Log.d(DEBUG_FLAG, "連上網路");

                scheduledExecutorService.submit(new NewsReceiveTask(context, false));
                scheduledExecutorService.submit(new LibOpenTimeReceiveTask(context));
                scheduledExecutorService.submit(new RecentActivityReceiveTask(context));
                scheduledExecutorService.submit(new FloorInfoReceiveTask(context));
                scheduledExecutorService.submit(new ContactInfoReceiveTask(context));
                scheduledExecutorService.submit(new Runnable() {
                    public static final String SYB_SUB_URL = "http://140.116.207.24/push/subscriptionStatus.php";

                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String username = sharedPreferences.getString(PreferenceKeys.USERNAME, "");
                        if(username.isEmpty()) return;
                        try {
                            String str = HttpClient.sendPost(SYB_SUB_URL, String.format("id=%s", username));

                            if (str.contains("Y")) {
                                sharedPreferences.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, true).apply();
                                Log.d(DEBUG_FLAG, "同步訂閱狀態 : Y");
                            } else if (str.contains("N")) {
                                sharedPreferences.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, false).apply();
                                Log.d(DEBUG_FLAG, "同步訂閱狀態 : N");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(DEBUG_FLAG, "同步訂閱狀態異常");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
