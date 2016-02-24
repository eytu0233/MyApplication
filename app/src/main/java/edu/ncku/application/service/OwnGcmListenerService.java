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

import edu.ncku.application.MainActivity;
import edu.ncku.application.R;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 此類別是繼承自GcmListenerService，用來實現GCM與APP的接口
 * 當GCM發送訊息給此APP時，將會呼叫onMessageReceived來處理訊息
 * 而sendNotification將會發出通知給使用者。
 */
public class OwnGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String DEBUG_FLAG = "OwnGcmListenerService";

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
        String message = data.getString("message");
        Log.d(DEBUG_FLAG, "From: " + from);
        Log.d(DEBUG_FLAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
                * Production applications would usually process the message here.
                * Eg: - Syncing with server.
                *     - Store message in local database.
                *     - Update UI.
                */

        /**
                * In some cases it may be useful to show a notification indicating to the user
                * that a message was received.
                */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message if and only if the user
     * has logged-in and subscribed.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

        if(!isSub()) return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private boolean isSub() {

        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        String username = SP.getString(PreferenceKeys.USERNAME, ""),
               password = SP.getString(PreferenceKeys.PASSWORD, "");
        boolean sub = SP.getBoolean(PreferenceKeys.SUBSCRIPTION, true);

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return sub;
        }

    }
}
