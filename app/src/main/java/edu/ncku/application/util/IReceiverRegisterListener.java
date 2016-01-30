package edu.ncku.application.util;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by NCKU on 2015/11/6.
 * 透過此介面來進行Receiver註冊與解除註冊的動作
 */
public interface IReceiverRegisterListener {

    public void onReceiverRegister(BroadcastReceiver receiver, IntentFilter filter);

    public void onReceiverUnregister(BroadcastReceiver receiver);

}
