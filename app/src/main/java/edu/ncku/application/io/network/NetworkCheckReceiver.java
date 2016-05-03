package edu.ncku.application.io.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import edu.ncku.application.service.RegistrationIntentService;
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

        try {

            if (context == null) return;

            ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            currentNetworkInfo = connectivityManager.getActiveNetworkInfo();


            /* 當連上網路時，在背景執行資料更新的工作 */
            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {

                Log.d(DEBUG_FLAG, "連上網路，自動更新資料");

                scheduledExecutorService.submit(new LibOpenTimeReceiveTask(context));
                scheduledExecutorService.submit(new RecentActivityReceiveTask(context));
                scheduledExecutorService.submit(new FloorInfoReceiveTask(context));
                scheduledExecutorService.submit(new ContactInfoReceiveTask(context));
                String deviceID = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.DEVICE_TOKEN, "");
                if(deviceID == null || deviceID.equals("")){
                    Log.d(DEBUG_FLAG, "背景執行GCM註冊");
                    RegistrationIntentService.startActionRegisterGCM(context);
                }else{
                    Log.d(DEBUG_FLAG, "GCM已註冊，故不重複註冊");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
