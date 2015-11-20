package edu.ncku.testapplication.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.concurrent.Executors;

public class NetworkCheckReceiver extends BroadcastReceiver {

    private static final String DEBUG_FLAG = NetworkCheckReceiver.class
            .getName();

    private NetworkInfo currentNetworkInfo;

    public NetworkCheckReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {

            if (context != null) {
                ConnectivityManager connectivityManager = ((ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE));
                currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                // do something when network connected.
                Log.d(DEBUG_FLAG, "連上網路");

                Executors.newScheduledThreadPool(1).submit(new NewsReceiveTask(true, context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
