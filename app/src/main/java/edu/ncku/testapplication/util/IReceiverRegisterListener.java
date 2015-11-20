package edu.ncku.testapplication.util;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by NCKU on 2015/11/6.
 */
public interface IReceiverRegisterListener {

    public void onReceiverRegister(BroadcastReceiver receiver, IntentFilter filter);

    public void onReceiverUnregister(BroadcastReceiver receiver);

}
