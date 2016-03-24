package edu.ncku.application.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.ncku.application.R;
import edu.ncku.application.service.SubscribeIntentService;
import edu.ncku.application.util.PreferenceKeys;

public class PrefFragment extends PreferenceFragment {

    private static final String DEBUG_FLAG = PrefFragment.class.getName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ProgressDialog progressDialog;

    private BroadcastReceiver mSubSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            // Get extra data included in the Intent
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog == null) return;
                    progressDialog.dismiss();
                    Toast.makeText(context, R.string.sub_handled, Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    };

    private BroadcastReceiver mSubFailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            // Get extra data included in the Intent
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog != null) progressDialog.dismiss();
                    Toast.makeText(context, R.string.sub_fail, Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    };

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.white));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this.getActivity().getWindow().getContext();

        this.addPreferencesFromResource((isLogin()) ? R.xml.preferences_login : R.xml.preferences_logout);

        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("MESSAGER_SUBSCRIPTION");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        broadcastManager.registerReceiver(mSubSuccessReceiver, new IntentFilter(PreferenceKeys.SUBSCRIPTIONS_HANDLE_COMPLETE));
        broadcastManager.registerReceiver(mSubFailReceiver, new IntentFilter(PreferenceKeys.SUBSCRIPTIONS_HANDLE_FAIL));

        final ConnectivityManager CM = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        if (checkboxPref != null)
            checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                public boolean onPreferenceChange(final Preference preference, Object newValue) {

                    final NetworkInfo info = CM.getActiveNetworkInfo();

                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_subscription)
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    checkboxCancel();
                                }
                            })
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if(info != null && info.isConnected()) {
                                        if (!checkPlayServices()) return;

                                        // Start IntentService to register this application with GCM.
                                        if (checkboxPref.isChecked()) {
                                            SubscribeIntentService.startActionSub(context);
                                            Log.d(DEBUG_FLAG, "SubscribeIntentService sub start!");
                                        } else {
                                            SubscribeIntentService.startActionUnsub(context);
                                            Log.d(DEBUG_FLAG, "SubscribeIntentService unsub start!");
                                        }
                                        progressDialog = ProgressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.handle_subscription), true);
                                    }else{
                                        checkboxCancel();
                                        Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).create().show();
                    return true;
                }

                private void checkboxCancel() {
                    boolean sub = sharedPreferences.getBoolean(PreferenceKeys.SUBSCRIPTION, true);
                    sharedPreferences.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, !sub).apply();
                    checkboxPref.setChecked(!sub);
                }
            });

        /* Below code is to avoid the default value not to be set */
        ListPreference preload_list_preference = (ListPreference) getPreferenceManager().findPreference("PRELOAD_MSGS_MAX");
        String preload_list_preference_value = preload_list_preference.getValue();
        if (preload_list_preference_value == null || preload_list_preference_value.equals("0")) {
            preload_list_preference.setValueIndex(0);
        }

        ListPreference load_list_preference = (ListPreference) getPreferenceManager().findPreference("LOAD_MSGS_MAX");
        String load_list_preference_value = load_list_preference.getValue();
        if (load_list_preference_value == null || load_list_preference_value.equals("0")) {
            load_list_preference.setValueIndex(0);
        }

        ListPreference out_of_date_preference = (ListPreference) getPreferenceManager().findPreference("OUT_OF_DATE");
        String out_of_date_preference_value = out_of_date_preference.getValue();
        if (out_of_date_preference_value == null || out_of_date_preference_value.equals("0")) {
            load_list_preference.setValueIndex(0);
        }

    }

    /**
     *
     * @return check this device  can use google play service
     */
    private boolean checkPlayServices() {

        final Context context = this.getActivity().getApplicationContext();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(DEBUG_FLAG, "This device is not supported.");
                Toast.makeText(context, R.string.device_unsupport, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    private boolean isLogin() {

        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(context);
        String username = SP.getString(PreferenceKeys.USERNAME, ""), password = SP.getString(PreferenceKeys.PASSWORD,
                "");

        Log.d(DEBUG_FLAG, "username : " + username);
        Log.d(DEBUG_FLAG, "password : " + password);

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }
}
