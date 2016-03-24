package edu.ncku.application.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashSet;

import edu.ncku.application.MainActivity;
import edu.ncku.application.R;
import edu.ncku.application.model.Message;
import edu.ncku.application.model.News;
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
    private static final String SUB_FILE_NAME = ".messages";
    private static final Object LOCKER = new Object();

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
        String message = data.getString("message");
        String control = data.getString("control");

        // 確認是否有控制指令
        if(control != null) {
            Log.d(DEBUG_FLAG, "Control: " + control);
            if(control.equals(LOGOUT_CTRL)) clearLogin(); // 收到登出指令，清除登入資料
            return;
        }

        if(title == null || title.isEmpty() ||
                time == null || time.isEmpty() ||
                message == null || message.isEmpty()) {
            Log.e(DEBUG_FLAG, "推播資料缺失");
            return;
        }

        Log.d(DEBUG_FLAG, "from : " + from);
        Log.d(DEBUG_FLAG, "Title : " + title);
        Log.d(DEBUG_FLAG, "Time : " + time);
        Log.d(DEBUG_FLAG, "Message : " + message);

        if(GLOBAL.equals(from)){
            Log.d(DEBUG_FLAG, "Handle Global Notification");
            if(synNewsFile(new News(title, "資訊服務組", Integer.valueOf(time), Integer.valueOf(time), message)) > 0)
            sendGlobalNotification(title);
            return;
        }

        if(username == null || username.isEmpty()) return;
        Log.d(DEBUG_FLAG, "username : " + username);

        // [START_EXCLUDE]
        /**
                * Production applications would usually process the message here.
                * Eg: - Syncing with server.
                *     - Store message in local database.
                *     - Update UI.
                */
        int position = synMsgFile(username, new Message(title, Integer.valueOf(time), message));
        if(position != -1){
            /**
                        * In some cases it may be useful to show a notification indicating to the user
                        * that a message was received.
                        */
            Log.d(DEBUG_FLAG, "position : " + position);
            sendNotification(username, title, position);
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message if and only if the user
     * has logged-in and subscribed.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String username, String message, int position) {

        if(!isSub(username)) return; // 沒有訂閱不發通知

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PreferenceKeys.MSGS_EXTRA, position);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendGlobalNotification(String message){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PreferenceKeys.GLOBAL_NEWS, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private int synMsgFile(String username, Message message) {

		/* Get internal storage directory */
        File dir = this.getApplicationContext().getFilesDir();
        File messagesFile = new File(dir, username + SUB_FILE_NAME); // 檔名是「學號.messages」，故不同使用者的檔案不同

        ObjectInputStream ois;
        ObjectOutputStream oos;
        LinkedHashSet<Message> messages;

        int position = -1;

        synchronized (LOCKER) { // 為避免有可能的race condition，以同步化區塊框之
            try {
                // read news data from file
                if (messagesFile.exists()) {
                    ois = new ObjectInputStream(new FileInputStream(messagesFile));
                    messages = (LinkedHashSet<Message>) ois.readObject();
                    if (ois != null)
                        ois.close();
                } else{
                    messages = new LinkedHashSet<Message>();
                }

                deleteOutOfDateMsgs(messages);

                int index = 0;
                if(messages != null && !messages.contains(message)) {
                    messages.add(message);
                    for(Message msg : messages){
                        if(msg == message) {
                            position = index;
                            break;
                        }
                        index++;
                    }
                }

                // 把更新後的資料寫入檔案
                oos = new ObjectOutputStream(new FileOutputStream(messagesFile));

                oos.writeObject(messages);
                oos.flush();
                if (oos != null)
                    oos.close();

                return position;

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                Log.e(DEBUG_FLAG, "The read object can't be found.");
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    private void deleteOutOfDateMsgs(LinkedHashSet<Message> messages) {
        final long ALIVE_DAYS = 90; // 存活天數
        final long SECONDS_OF_A_DAY = 24 * 60 * 60; // 一天秒數
        final long OUT_OF_DATE_TIMESTAMP = ALIVE_DAYS * SECONDS_OF_A_DAY; // 時間戳記的差值

        long nowTimeStamp = System.currentTimeMillis() / 1000L; // 取得當前時間戳記

        LinkedHashSet<Message> deleteMsgsSet = new LinkedHashSet<Message>();

        for(Message msg : messages){
            if(nowTimeStamp - (long)msg.getEndTime() >= OUT_OF_DATE_TIMESTAMP){
                deleteMsgsSet.add(msg);
            }
        }

        if(!deleteMsgsSet.isEmpty()) messages.removeAll(deleteMsgsSet); // 刪除過期最新消息
    }

    /**
     *  將最新消息資料存進檔案，但之中有重複的最新消息
     *  不會多次存取只留一個。並且刪除超過保存時間的最新消息。
     *
     * @param news 來自GCM的緊急消息
     * @return 新增的最新消息數量(假如都重複則為0)
     */
    private int synNewsFile(News news) {

		/* Get internal storage directory */
        File dir = this.getApplicationContext().getFilesDir();
        File newsFile = new File(dir, "News");

        ObjectInputStream ois;
        ObjectOutputStream oos;
        LinkedHashSet<News> readNews, mergeReadNews; // 宣告成有序集合(不重複且加入有順序)

        int updateNum = 0;

        synchronized (LOCKER) { // 為避免有可能的race condition 以同步化區塊框之
            try {
                // read news data from file
                if (newsFile.exists()) {
                    ois = new ObjectInputStream(new FileInputStream(newsFile));
                    readNews = (LinkedHashSet<News>) ois.readObject();
                    if (ois != null)
                        ois.close();
                } else{
                    readNews = new LinkedHashSet<News>();
                }

                // record the old number of news
                int oldNum = readNews.size();
                // merges two news set to readNews
                mergeReadNews = new LinkedHashSet<News>();
                mergeReadNews.add(news);
                mergeReadNews.addAll(readNews);

                updateNum = mergeReadNews.size() - oldNum;

                // overwrite the news data to the file
                oos = new ObjectOutputStream(new FileOutputStream(newsFile));
                oos.writeObject(mergeReadNews);
                oos.flush();
                if (oos != null)
                    oos.close();

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                Log.e(DEBUG_FLAG, "The read object can't be found.");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return updateNum;
    }

    private void clearLogin(){
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SP.edit().remove(PreferenceKeys.USERNAME).apply();
        SP.edit().remove(PreferenceKeys.PASSWORD).apply();
    }

    private boolean isSub(String notifyUsername) {

        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        String username = SP.getString(PreferenceKeys.USERNAME, ""),
                password = SP.getString(PreferenceKeys.PASSWORD, "");
        boolean sub = SP.getBoolean(PreferenceKeys.SUBSCRIPTION, true);

        if (username.isEmpty() || password.isEmpty() || !username.equals(notifyUsername)) {
            return false;
        } else {
            return sub;
        }

    }

}
