package edu.ncku.application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import edu.ncku.application.util.DrawerListSelector;
import edu.ncku.application.util.Preference;

public class LoginDialog extends DialogFragment {

    private static final String DEBUG_FLAG = LoginDialog.class.getName();

    /* UI components */
    private Button mBtnLogin, mBtnCancel;
    private EditText mEditUsername, mEditPassword;
    private TextView mTxtTip;
    private ProgressBar mPBLogin;

    private DrawerListSelector drawerListSelector;
    private Context context;
    private Handler handler = new Handler();

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
//        View v = inflater.inflate(R.layout.fragment_login, null);
        View v = inflater.inflate(R.layout.fragment_login, null);
        mBtnLogin = (Button) v.findViewById(R.id.btnLogin);
//        mBtnCancel = (Button) v.findViewById(R.id.btnCancel);
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTxtTip.setText(R.string.invalid_account_or_password);
                            mPBLogin.setVisibility(View.INVISIBLE);
                            mBtnLogin.setVisibility(View.VISIBLE);
                        }
                    });
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

                LoginTask loginTask = new LoginTask(context);
                loginTask.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, username, password);
                String name = "";
                try {
                    name = loginTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    if (name == null) name = "";
                }

                setLogining(false);

                if (!name.isEmpty()) {
                    drawerListSelector.loginState(name); // 透過drawerListSelector來改變drawer狀態
                    LoginDialog.this.dismiss();

                    /* 存進設定值 */
                    Preference.setName(context, name);
                    Preference.setUsername(context, username);
                    Preference.setPassword(context, password);
                    Preference.setSubscription(context, false);

                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
                } else {
                    mTxtTip.setText(R.string.invalid_account_or_password);
                }
            }

        });

        /*mBtnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isLogining()) return;
                dismiss();
            }

        });*/
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
    private synchronized void setLogining(final boolean logining) {

        this.logining = logining;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /* 登入按鈕的轉換 */
                mPBLogin.setVisibility((logining)?View.VISIBLE:View.INVISIBLE);
                mBtnLogin.setVisibility((logining)?View.INVISIBLE:View.VISIBLE);
            }
        }, (logining)?0:2000);
    }

}
