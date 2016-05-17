package edu.ncku.application.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import edu.ncku.application.R;
import edu.ncku.application.io.network.SubscribeTask;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 設定頁面，會根據登入狀態來決定要載入的XML資源檔
 */
public class PrefFragment extends PreferenceFragment {

    private static final String DEBUG_FLAG = PrefFragment.class.getName();

    private ProgressDialog progressDialog;

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

        this.addPreferencesFromResource((edu.ncku.application.util.Preference.isLoggin(context)) ? R.xml.preferences_login : R.xml.preferences_logout);

        final SwitchPreference switchPref = (SwitchPreference) getPreferenceManager().findPreference(PreferenceKeys.SUBSCRIPTION);

        final ConnectivityManager CM = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (switchPref != null) // 註冊switchPref的狀態改變事件
            switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d(DEBUG_FLAG, "" + switchPref.isChecked());
                    final NetworkInfo info = CM.getActiveNetworkInfo();

                    /* 判斷網路狀態 */
                    if (info != null && info.isConnected()) {

                        // Start IntentService to register this application with GCM.
                        progressDialog = ProgressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.handle_subscription), true); // 顯示處理中的Dialog
                        SubscribeTask subscribeTask = new SubscribeTask(context);
                        subscribeTask.execute(!switchPref.isChecked());
                        Boolean check;
                        try {
                            check = subscribeTask.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            check = false;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            check = false;
                        }

                        if (check == null) check = false;

                        final boolean transCheck = check;
                        /* 更新UI */
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) progressDialog.dismiss(); // 關閉顯示處理中的Dialog
                                Toast.makeText(context, (transCheck) ? R.string.sub_handled : R.string.sub_fail, Toast.LENGTH_LONG).show(); // 顯示Toast
                            }
                        }, 1000);

                        return transCheck;
                    } else {
                        Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            });
    }
}
