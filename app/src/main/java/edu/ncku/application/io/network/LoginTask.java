package edu.ncku.application.io.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import edu.ncku.application.LoginDialog;
import edu.ncku.application.R;
import edu.ncku.application.service.RegistrationIntentService;
import edu.ncku.application.util.DrawerListSelector;
import edu.ncku.application.util.EnvChecker;
import edu.ncku.application.util.Preference;
import edu.ncku.application.util.PreferenceKeys;
import edu.ncku.application.util.Security;

/**
 * 在背景執行登入驗證工作
 */
public class LoginTask extends AsyncTask<String, Void, String> {

    private static final String DEBUG_FLAG = LoginTask.class.getName();

    private static final String LOGIN_URL = "http://140.116.207.24/push/login.php";
    private static final String OK = "OK";
    private static final String RESULT_LABEL = "Result";
    private static final String NAME_LABEL = "Name";

    private Context context;
    private LoginDialog loginDialog;
    private DrawerListSelector drawerListSelector;
    private SharedPreferences sharedPreferences;

    private String username;

    public LoginTask(Context context, LoginDialog loginDialog, DrawerListSelector drawerListSelector) {
        super();
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.loginDialog = loginDialog;
        this.drawerListSelector = drawerListSelector;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub

        String result = "";

        // 檢查參數值
        if (params == null || params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
            return result;
        }

        username = params[0].toUpperCase();
        String password = params[1];

        try {
            Log.d(DEBUG_FLAG, "username : " + username);
            Log.d(DEBUG_FLAG, "password : " + (new Security()).encrypt(password));
            String str = HttpClient.sendPost(LOGIN_URL, String.format("username=%s&password=%s", username, URLEncoder.encode((new Security()).encrypt(password)), "UTF-8"));
            JSONObject json = new JSONObject(str);

            if (OK.equals(json.getString(RESULT_LABEL))) {
                result = json.getString(NAME_LABEL);
                Log.d(DEBUG_FLAG, "Name : " + result);

                // 如果發現DeviceID從未傳送到Server或被清除則重新從GCM Server取得DeviceID
                String deviceID = sharedPreferences.getString(PreferenceKeys.DEVICE_TOKEN, "");
                if (deviceID == null || deviceID.equals("")) {
                    Log.d(DEBUG_FLAG, "登入時發現沒有註冊GCM，故啟動註冊背景服務");
                    RegistrationIntentService.startActionRegisterGCM(context);
                }
            }

        } catch (JSONException e) {
            Log.w(DEBUG_FLAG, "登入資訊Json格式解析錯誤或登入失敗");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(DEBUG_FLAG, e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(final String name) {
        super.onPostExecute(name);

        loginDialog.setLogining(false);

        if (!name.isEmpty()) {
            drawerListSelector.loginState(name); // 透過drawerListSelector來改變drawer狀態
            loginDialog.dismiss();

            /* 存進設定值 */
            Preference.setName(context, name);
            Preference.setUsername(context, username);
            Preference.setSubscription(context, false);

//            (new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.subDialog)))
            (new AlertDialog.Builder(context))
                    .setMessage(R.string.sub_hint)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            final ProgressDialog progressDialog;
                            if (EnvChecker.isNetworkConnected(context)) {

                                // Start IntentService to register this application with GCM.
                                progressDialog = ProgressDialog.show(context, context.getString(R.string.please_wait), context.getString(R.string.handle_subscription), true); // 顯示處理中的Dialog
                                SubscribeTask subscribeTask = new SubscribeTask(context);
                                subscribeTask.execute(true);
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
                                Preference.setSubscription(context, transCheck);

                                /* 更新UI */
                                        (new Handler()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (progressDialog != null)
                                                    progressDialog.dismiss(); // 關閉顯示處理中的Dialog
                                                Toast.makeText(context, (transCheck) ? R.string.sub_handled : R.string.sub_fail, Toast.LENGTH_LONG).show(); // 顯示Toast
                                            }
                                        }, 1000);

                            } else {
                                Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    }).show();

            Toast.makeText(context, R.string.login_success, Toast.LENGTH_LONG).show();
        } else {
            loginDialog.mTxtTip.setText(R.string.invalid_account_or_password);
        }
    }
}
