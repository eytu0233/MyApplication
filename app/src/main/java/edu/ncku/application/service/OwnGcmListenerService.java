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

        /* 確認是否有控制指令 */
        if(control != null) {
            Log.d(DEBUG_FLAG, "Control: " + control);
            if(control.equals(LOGOUT_CTRL)) logout(); // 收到登出指令，清除登入資料
            return;
        }

        /* 確認是否為廣播資料 */
        if(GLOBAL.equals(from) && title != null && !title.isEmpty()){
            Log.d(DEBUG_FLAG, "Handle Global Notification");
            sendGlobalNotification(title);
            return;
        }

        /* 確認推播訊息的完整性 */
        if(time == null || time.isEmpty() ||
                msgNo == null || msgNo.isEmpty()) {
            Log.e(DEBUG_FLAG, "推播資料缺失");
            return;
        }

        Log.d(DEBUG_FLAG, "from : " + from);
        Log.d(DEBUG_FLAG, "Time : " + time);
        Log.d(DEBUG_FLAG, "msgNo : " + msgNo);

        /* 如果沒有登入則不顯示推播通知 */
        if(username == null || username.isEmpty()) return;
        Log.d(DEBUG_FLAG, "username : " + username);

        /* 將推播儲存起來 */
        Executors.newSingleThreadExecutor().submit(new MsgReceiveTask(this.getApplicationContext(), username, msgNo, Integer.valueOf(time)));
    }
    // [END receive_message]

    private void sendGlobalNotification(String message){
        /* 設置通知點擊會啟動App Intent */
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PreferenceKeys.GLOBAL_NEWS, true); // 告訴Activity要開啟最新消息頁面
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis())); // 時間差
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)   通知聲音關閉
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(message.hashCode() /* ID of notification */, notificationBuilder.build());
    }

    /**
     * 清除登入資訊來表示登出
     */
    private void logout(){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SP.edit().remove(PreferenceKeys.ACCOUNT).apply();
        Log.d(DEBUG_FLAG, "logout");
    }

}
