package edu.ncku.application.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Executors;

import edu.ncku.application.MainActivity;
import edu.ncku.application.R;
import edu.ncku.application.io.network.MsgReceiveTask;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 此類別是繼承自GcmListenerService，用來實現GCM與APP的接口
 * 當GCM發送訊息給此APP時，將會呼叫onMessageReceived來處理訊息
 * 而sendNotification將會發出通知給使用者。
 */
public class OwnGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String DEBUG_FLAG = "OwnGcmListenerService";
    private static final String GLOBAL = "/topics/global";
    private static final String LOGOUT_CTRL = "logout";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String username = data.getString("user");
        String title = data.getString("title");
        String time = data.getString("time");
        String msgNo = data.getString("msgNo");
        String control = data.getString("control");

        // 確認是否有控制指令
        if(control != null) {
            Log.d(DEBUG_FLAG, "Control: " + control);
            if(control.equals(LOGOUT_CTRL)) logout(); // 收到登出指令，清除登入資料
            return;
        }

        if(GLOBAL.equals(from) && title != null && !title.isEmpty()){
            Log.d(DEBUG_FLAG, "Handle Global Notification");
            sendGlobalNotification(title);
            return;
        }

        if(time == null || time.isEmpty() ||
                msgNo == null || msgNo.isEmpty()) {
            Log.e(DEBUG_FLAG, "推播資料缺失");
            return;
        }

        Log.d(DEBUG_FLAG, "from : " + from);
        Log.d(DEBUG_FLAG, "Time : " + time);
        Log.d(DEBUG_FLAG, "msgNo : " + msgNo);

        if(username == null || username.isEmpty()) return;
        Log.d(DEBUG_FLAG, "username : " + username);

        Executors.newSingleThreadExecutor().submit(new MsgReceiveTask(this.getApplicationContext(), username, msgNo, Integer.valueOf(time)));
    }
    // [END receive_message]

    private void sendGlobalNotification(String message){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PreferenceKeys.GLOBAL_NEWS, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(message.hashCode() /* ID of notification */, notificationBuilder.build());
    }

    private void logout(){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SP.edit().remove(PreferenceKeys.ACCOUNT).apply();
        Log.d(DEBUG_FLAG, "logout");
    }

}
