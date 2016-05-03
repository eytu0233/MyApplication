package edu.ncku.application.io.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import edu.ncku.application.MainActivity;
import edu.ncku.application.R;
import edu.ncku.application.model.Message;
import edu.ncku.application.util.PreferenceKeys;

/**
 * Created by NCKU on 2016/4/25.
 */
public class MsgReceiveTask extends JsonReceiveTask implements Runnable {

    private static final String DEBUG_FLAG = MsgReceiveTask.class.getName();
    private static final Object LOCKER = new Object();
    private static final String SUB_FILE_NAME = ".messages";
    private static final String JSON_URL = "http://140.116.207.24/push/msg_json.php?msgNo=";

    private String username;
    private String json_url;
    private int publishTimestamp;

    public MsgReceiveTask(Context mContext, String username, String msgNo, int publishTimestamp) {
        super(mContext);
        this.username = username;
        this.json_url = JSON_URL + msgNo;
        this.publishTimestamp = publishTimestamp;
    }

    @Override
    public void run() {
        try {
            JSONObject json = new JSONObject(jsonRecieve(json_url)); // 透過父類別方法jsonRecieve取得JSON物件

            String title = json.getString("Title");
            String content = json.getString("Content");

            int position = synMsgFile(username, new Message(title, publishTimestamp, content));
            Log.d(DEBUG_FLAG, "position : " + position);
            sendNotification(username, title, position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message if and only if the user
     * has logged-in and subscribed.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String username, String message, int position) {

        if (!isSub(username)) return; // 沒有訂閱不發通知

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(PreferenceKeys.MSGS_EXTRA, position);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(position /* ID of notification */, notificationBuilder.build());
    }

    private boolean isSub(String notifyUsername) {

        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        String username = SP.getString(PreferenceKeys.USERNAME, ""),
                password = SP.getString(PreferenceKeys.PASSWORD, "");
        boolean sub = SP.getBoolean(PreferenceKeys.SUBSCRIPTION, true);

        if (username.isEmpty() || password.isEmpty() || !username.equals(notifyUsername)) {
            return false;
        } else {
            return sub;
        }

    }

    private int synMsgFile(String username, Message message) {

		/* Get internal storage directory */
        File dir = mContext.getFilesDir();
        File messagesFile = new File(dir, username + SUB_FILE_NAME); // 檔名是「學號.messages」，故不同使用者的檔案不同

        ObjectInputStream ois;
        ObjectOutputStream oos;
        LinkedList<Message> messages;

        int position = -1;

        synchronized (LOCKER) { // 為避免有可能的race condition，以同步化區塊框之
            try {
                // read news data from file
                if (messagesFile.exists()) {
                    ois = new ObjectInputStream(new FileInputStream(messagesFile));
                    messages = (LinkedList<Message>) ois.readObject();
                    if (ois != null)
                        ois.close();
                } else {
                    messages = new LinkedList<Message>();
                }

                if (messages != null) {
                    messages.addFirst(message);
                }

                // 把更新後的資料寫入檔案
                oos = new ObjectOutputStream(new FileOutputStream(messagesFile));

                oos.writeObject(messages);
                oos.flush();
                if (oos != null)
                    oos.close();

                return messages.size() - 1;

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
}
