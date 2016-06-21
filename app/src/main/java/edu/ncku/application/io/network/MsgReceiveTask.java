package edu.ncku.application.io.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import edu.ncku.application.util.Preference;
import edu.ncku.application.util.PreferenceKeys;

/**
 * Created by NCKU on 2016/4/25.
 * 透過來自GCM傳送的資料(msgNo)，向圖書館伺服器取得推播訊息的內容
 */
public class MsgReceiveTask extends JsonReceiveTask implements Runnable {

    private static final String DEBUG_FLAG = MsgReceiveTask.class.getName();
    private static final Object LOCKER = new Object();
    private static final String SUB_FILE_NAME = ".messages";
    private static final String JSON_URL = "http://140.116.207.24/push/msg_json.php?msgNo=%s&os=A&recver=%s&did=%s";

    private String account;
    private String json_url;
    private int publishTimestamp; // 時間戳記

    public MsgReceiveTask(Context mContext, String account, String msgNo, int publishTimestamp) {
        super(mContext);
        this.account = account.toUpperCase();
        this.json_url = String.format(JSON_URL, msgNo, account, Preference.getDeviceID(mContext));
        this.publishTimestamp = publishTimestamp;
    }

    @Override
    public void run() {
        try {
            JSONObject json = new JSONObject(jsonRecieve(json_url)); // 透過父類別方法jsonRecieve取得JSON物件

            String title = json.getString("Title"); // 從Json的物件當中取得標題
            String content = json.getString("Content"); // 從Json的物件當中取得內容

            int position = synMsgFile(account, new Message(title, publishTimestamp, content));
            Log.d(DEBUG_FLAG, "position : " + position);
            sendNotification(account, title, position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 發送Notification，並夾帶該封推播訊息的位置，以方便之後的點擊事件
     *
     * @param message GCM message received.
     */
    private void sendNotification(String account, String message, int position) {

        if (!Preference.isSub(mContext, account)) return; // 沒有訂閱不發通知

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(PreferenceKeys.MSGS_EXTRA, position);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(position /* ID of notification */, notificationBuilder.build());
    }

    /**
     * 將取得的推播訊息存進檔案之中
     *
     * @param account 帳號
     * @param message 封裝好的推播訊息物件
     * @return 該封推播訊息在檔案中的位置
     */
    private int synMsgFile(String account, Message message) {

		/* Get internal storage directory */
        File dir = mContext.getFilesDir();
        File messagesFile = new File(dir, account + SUB_FILE_NAME); // 檔名是「學號.messages」，故不同使用者的檔案不同

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
