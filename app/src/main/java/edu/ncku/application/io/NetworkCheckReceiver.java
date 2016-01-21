package edu.ncku.application.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NetworkCheckReceiver extends BroadcastReceiver {

    private static final String DEBUG_FLAG = NetworkCheckReceiver.class
            .getName();

    private NetworkInfo currentNetworkInfo;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

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

                scheduledExecutorService.submit(new NewsReceiveTask(context, false));
                scheduledExecutorService.submit(new LibOpenTimeReceiveTask(context));
                scheduledExecutorService.submit(new RecentActivityReceiveTask(context));
                scheduledExecutorService.submit(new FloorInfoReceiveTask(context));
                scheduledExecutorService.submit(new ContactInfoReceiveTask(context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
