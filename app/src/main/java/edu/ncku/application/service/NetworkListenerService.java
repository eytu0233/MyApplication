package edu.ncku.application.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import edu.ncku.application.io.network.NetworkCheckReceiver;

/**
 * 此Service類別不會綁定本Application，意即此Service將不會隨著
 * Activity而消滅，此Service是用來監視網路狀態的類別，將會在
 * 網路狀態改變時被驅動，主要目的是在網路連上去的時候，在
 * 背景執行工作。
 */
public class NetworkListenerService extends Service {

    private static final String DEBUG_FLAG = NetworkListenerService.class.getName();

    private final IBinder mBinder = new MyBinder();

    /* 此Receiver能夠監視網路改變的狀態(需註冊) */
    private NetworkCheckReceiver mNetworkStateReceiver = new NetworkCheckReceiver();

    public NetworkListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(DEBUG_FLAG, "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        try {
            /* 註冊Receiver */
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkStateReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(DEBUG_FLAG, "onDestroy");
        unregisterReceiver(mNetworkStateReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(DEBUG_FLAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        NetworkListenerService getService() {
            return NetworkListenerService.this;
        }
    }
}
