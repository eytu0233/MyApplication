package edu.ncku.application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import edu.ncku.application.io.network.LoginTask;
import edu.ncku.application.io.network.SubscribeTask;
import edu.ncku.application.util.DrawerListSelector;
import edu.ncku.application.util.PreferenceKeys;

public class LoginDialog extends DialogFragment {

    private static final String DEBUG_FLAG = LoginDialog.class.getName();

    /* UI components */
    private Button mBtnLogin, mBtnCancel;
    private EditText mEditUsername, mEditPassword;
    private TextView mTxtTip;
    private ProgressBar mPBLogin;

    private DrawerListSelector drawerListSelector;
    private Context context;

    private boolean logining = false;

    /**
     * Constructor
     *
     * @param drawerListSelector
     * @param context
     */
    public LoginDialog(DrawerListSelector drawerListSelector, Context context) {
        this.drawerListSelector = drawerListSelector;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_login, null);
        mBtnLogin = (Button) v.findViewById(R.id.btnLogin);
        mBtnCancel = (Button) v.findViewById(R.id.btnCancel);
        mEditUsername = (EditText) v.findViewById(R.id.editTextID);
        mEditPassword = (EditText) v.findViewById(R.id.editTextPassword);
        mTxtTip = (TextView) v.findViewById(R.id.txtTip);
        mPBLogin = (ProgressBar) v.findViewById(R.id.progressBarLogin);

        setEventListenner();

		/* 建立AlertDialog實體 */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(v).create();
    }

    private void setEventListenner() {
        // TODO Auto-generated method stub
        mBtnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setLogining(true); // 將狀態變成登入中
                mPBLogin.setVisibility(View.VISIBLE); // 將mPBLogin顯示
                mBtnLogin.setVisibility(View.INVISIBLE); // 將mBtnLogin隱藏

				/* 從設定值取得帳號跟密碼 */
                final String username = mEditUsername.getText().toString(), password = mEditPassword
                        .getText().toString();
                /* 取得當前的網路連線狀態 */
                ConnectivityManager CM = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = CM.getActiveNetworkInfo();

				/* 判斷是否有欄位沒有填寫 */
                if ((username != null && "".equals(username))
                        || (password != null && "".equals(password))) {
                    mTxtTip.setText(R.string.invalid_account_or_password);
                    mPBLogin.setVisibility(View.INVISIBLE);
                    mBtnLogin.setVisibility(View.VISIBLE);
                    setLogining(false);
                    return;
                }

				/* 判斷網路是否有連線 */
                if (info == null || !info.isConnected()) {
                    mTxtTip.setText(R.string.network_disconnected);
                    mPBLogin.setVisibility(View.INVISIBLE);
                    mBtnLogin.setVisibility(View.VISIBLE);
                    setLogining(false);
                    return;
                }

				/* 實作LoginTask中的ILoginResult介面 */
                final Context context = getActivity().getApplicationContext();
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//                LoginTask loginTask = new LoginTask(context, new ILoginResultListener() {
//
//                    @Override
//                    public void loginEvent(final boolean isLogin) {
//                        // TODO Auto-generated method stub
//                        /* 完成登入工作後，會藉由此方法取得結果 */
//                        (new Handler()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//							    /* 結束登入工作 */
//                                mPBLogin.setVisibility(View.INVISIBLE);
//                                mBtnLogin.setVisibility(View.VISIBLE);
//
//							    /* 登入成功 */
//                                if (isLogin) {
//                                    drawerListSelector.loginState(); // 透過drawerListSelector來改變drawer狀態
//                                    LoginDialog.this.dismiss();
//
//								    /* 將帳號密碼存進設定值 */
//                                    sharedPreferences.edit().putString(PreferenceKeys.USERNAME, username).apply();
//                                    sharedPreferences.edit().putString(PreferenceKeys.PASSWORD, password).apply();
//
//                                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
//
//                                    new AlertDialog.Builder(context)
//                                            .setMessage(R.string.login_sub_tip)
//                                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    sharedPreferences.edit().putBoolean("MESSAGER_SUBSCRIPTION", false).apply();
//                                                }
//                                            })
//                                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    final ProgressDialog progressDialog = ProgressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.handle_subscription), true);
//                                                    SubscribeTask subscribeTask = new SubscribeTask(context);
//                                                    subscribeTask.execute(true);
//                                                    Boolean check;
//                                                    try {
//                                                        check = subscribeTask.get();
//                                                    } catch (InterruptedException e) {
//                                                        e.printStackTrace();
//                                                        check = false;
//                                                    } catch (ExecutionException e) {
//                                                        e.printStackTrace();
//                                                        check = false;
//                                                    }
//
//                                                    if (check == null) check = false;
//                                                    if (progressDialog != null)
//                                                        progressDialog.dismiss();
//                                                    Toast.makeText(context, (check) ? R.string.sub_handled : R.string.sub_fail, Toast.LENGTH_SHORT).show();
//
//                                                    sharedPreferences.edit().putBoolean("MESSAGER_SUBSCRIPTION", check).apply();
//                                                }
//                                            }).create().show();
//                                } else {
//                                    mTxtTip.setText(R.string.invalid_account_or_password);
//                                }
//
//                                setLogining(false);
//                            }
//                        }, 1000);
//                    }
//
//                });
                LoginTask loginTask = new LoginTask(context);
                loginTask.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, username, password);
                Boolean isLogin = null;
                try {
                    isLogin = loginTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isLogin = false;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    isLogin = false;
                } finally {
                    if (isLogin == null) isLogin = false;
                }

                if (isLogin) {
                    drawerListSelector.loginState(); // 透過drawerListSelector來改變drawer狀態
                    LoginDialog.this.dismiss();

								    /* 將帳號密碼存進設定值 */
                    sharedPreferences.edit().putString(PreferenceKeys.USERNAME, username).apply();
                    sharedPreferences.edit().putString(PreferenceKeys.PASSWORD, password).apply();

                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(context)
                            .setMessage(R.string.login_sub_tip)
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    sharedPreferences.edit().putBoolean("MESSAGER_SUBSCRIPTION", false).apply();
                                }
                            })
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final ProgressDialog progressDialog = ProgressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.handle_subscription), true);
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
                                    if (progressDialog != null)
                                        progressDialog.dismiss();
                                    Toast.makeText(context, (check) ? R.string.sub_handled : R.string.sub_fail, Toast.LENGTH_SHORT).show();

                                    sharedPreferences.edit().putBoolean("MESSAGER_SUBSCRIPTION", check).apply();
                                }
                            }).create().show();
                } else {
                    mTxtTip.setText(R.string.invalid_account_or_password);
                }
            }

        });

        mBtnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isLogining()) return;
                dismiss();
            }

        });
    }

    /**
     * @return 當前是否處在登入中的狀態
     */
    private synchronized boolean isLogining() {
        return logining;
    }

    /**
     * @param logining 設定當前是否處在登入中的狀態
     */
    private synchronized void setLogining(boolean logining) {
        this.logining = logining;
    }

    private void setSubState(boolean sub) {

    }
}
