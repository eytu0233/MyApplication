package edu.ncku.testapplication.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.concurrent.Executors;

import edu.ncku.testapplication.io.NetworkCheckReceiver;
import edu.ncku.testapplication.io.NewsReceiveTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NewsRecieveService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    private static final String DEBUG_FLAG = NewsRecieveService.class.getName();

    private static final String ACTION_NORMAL = "edu.ncku.testapplication.service.action.NORMAL";
    private static final String ACTION_ONCE = "edu.ncku.testapplication.service.action.ONCE";
    private static final String ACTION_UNREGISTER = "edu.ncku.testapplication.service.action.UNREGISTER";

    private NetworkCheckReceiver mNetworkStateReceiver = new NetworkCheckReceiver();

    /**
     * Starts this service to perform action NORMAL with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionNORMAL(Context context) {
        try {
            Intent intent = new Intent(context, NewsRecieveService.class);
            intent.setAction(ACTION_NORMAL);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts this service to perform action ONCE with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionONCE(Context context) {
        try {
            Intent intent = new Intent(context, NewsRecieveService.class);
            intent.setAction(ACTION_ONCE);
            context.startService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts this service to perform action UNREGISTER with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUNREGISTER(Context context) {
        try {
            Intent intent = new Intent(context, NewsRecieveService.class);
            intent.setAction(ACTION_UNREGISTER);
            context.startService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NewsRecieveService() {
        super("NewsRecieveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NORMAL.equals(action)) {
                handleActionNORMAL();
            } else if (ACTION_ONCE.equals(action)) {
                handleActionONCE();
            } else if (ACTION_UNREGISTER.equals(action)) {
                handleActionUNREGISTER();
            }
        }
    }

    /**
     * Handle action NORMAL in the provided background thread with the provided
     * parameters.
     */
    private void handleActionNORMAL() {
        // TODO: Handle action NORMAL
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkStateReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action ONCE in the provided background thread with the provided
     * parameters.
     */
    private void handleActionONCE() {
        // TODO: Handle action
        try {
            Executors.newScheduledThreadPool(1).submit(new NewsReceiveTask(true,
                    getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action UNREGISTER in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUNREGISTER() {
        try {
            unregisterReceiver(mNetworkStateReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(DEBUG_FLAG, "Receiver not registered : mNetworkStateReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        handleActionUNREGISTER();
        super.onDestroy();
    }
}
