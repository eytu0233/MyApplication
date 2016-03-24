package edu.ncku.application.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import edu.ncku.application.io.network.HttpClient;
import edu.ncku.application.util.PreferenceKeys;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SubscribeIntentService extends IntentService {

    private static final String DEBUG_FLAG = SubscribeIntentService.class.getName();

    private static final String ACTION_SUB = "edu.ncku.application.service.action.SUB";
    private static final String ACTION_UNSUB = "edu.ncku.application.service.action.UNSUB";

    private static final String SUB_URL = "http://140.116.207.24/push/subscription.php";

    public SubscribeIntentService() {
        super("SubscribeIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSub(Context context) {
        Intent intent = new Intent(context, SubscribeIntentService.class);
        intent.setAction(ACTION_SUB);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUnsub(Context context) {
        Intent intent = new Intent(context, SubscribeIntentService.class);
        intent.setAction(ACTION_UNSUB);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SUB.equals(action)) {
                handleActionSub();
            } else if (ACTION_UNSUB.equals(action)) {
                handleActionUnsub();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSub() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        LocalBroadcastManager broadcast = LocalBroadcastManager.getInstance(this);
        try {
            String username = sp.getString(PreferenceKeys.USERNAME, "");
            String did = sp.getString(PreferenceKeys.DEVICE_TOKEN, "");
            if(username != null && did != null && !username.isEmpty() && !did.isEmpty()){ // 避免因狀態改變的自動登出導致抓不到username
                if(!HttpClient.sendPost(SUB_URL, String.format("id=%s&did=%s&os=A&sub=1", username, did)).contains("OK")) throw new Exception("Posting data to server failed");
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PreferenceKeys.SUBSCRIPTIONS_HANDLE_COMPLETE));
            }else{
                throw new Exception("Username or deviceID is wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Notify UI that registration failed, so the progress indicator can be hidden.
            broadcast.sendBroadcast(new Intent(PreferenceKeys.SUBSCRIPTIONS_HANDLE_FAIL));
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUnsub() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        LocalBroadcastManager broadcast = LocalBroadcastManager.getInstance(this);
        try {
            String username = sp.getString(PreferenceKeys.USERNAME, "");
            String did = sp.getString(PreferenceKeys.DEVICE_TOKEN, "");
            if(username != null && did != null && !username.isEmpty() && !did.isEmpty()){ // 避免因狀態改變的自動登出導致抓不到username
                if(!HttpClient.sendPost(SUB_URL, String.format("id=%s&did=%s&os=A&sub=0", username, did)).contains("OK")) throw new Exception("Posting data to server failed");
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PreferenceKeys.SUBSCRIPTIONS_HANDLE_COMPLETE));
            }else{
                throw new Exception("Username or deviceID is wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Notify UI that registration failed, so the progress indicator can be hidden.
            broadcast.sendBroadcast(new Intent(PreferenceKeys.SUBSCRIPTIONS_HANDLE_FAIL));
        }
    }
}
