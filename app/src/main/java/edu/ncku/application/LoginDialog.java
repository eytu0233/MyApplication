package edu.ncku.application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.ncku.application.io.network.LoginTask;
import edu.ncku.application.util.DrawerListSelector;
import edu.ncku.application.util.EnvChecker;

public class LoginDialog extends DialogFragment {

    private static final String DEBUG_FLAG = LoginDialog.class.getName();

    /* UI components */
    private Button mBtnLogin;
    private EditText mEditUsername, mEditPassword;
    public TextView mTxtTip;
    public ProgressBar mPBLogin;

    private DrawerListSelector drawerListSelector;
    private Context context;
    private Handler handler = new Handler();

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
        mEditUsername = (EditText) v.findViewById(R.id.editTextID);
        mEditPassword = (EditText) v.findViewById(R.id.editTextPassword);
        mEditPassword.setTypeface(Typeface.DEFAULT);
        mEditPassword.setTransformationMethod(new PasswordTransformationMethod());
        mTxtTip = (TextView) v.findViewById(R.id.txtTip);
        mPBLogin = (ProgressBar) v.findViewById(R.id.progressBarLogin);

        setLonginButtonListenner();

		/* 建立AlertDialog實體 */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(v).create();
    }

    private void setLonginButtonListenner() {
        // TODO Auto-generated method stub
        mBtnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setLogining(true); // 將狀態變成登入中

				/* 從設定值取得帳號跟密碼 */
                final String username = mEditUsername.getText().toString(), password = mEditPassword
                        .getText().toString();

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
                if (!EnvChecker.isNetworkConnected(context)) {
                    mTxtTip.setText(R.string.network_disconnected);
                    mPBLogin.setVisibility(View.INVISIBLE);
                    mBtnLogin.setVisibility(View.VISIBLE);
                    setLogining(false);
                    return;
                }

                LoginTask loginTask = new LoginTask(context, LoginDialog.this, drawerListSelector);
                loginTask.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, username, password);

            }

        });
    }

    /**
     * @param logining 設定當前是否處在登入中的狀態
     */
    public synchronized void setLogining(final boolean logining) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /* 登入按鈕的轉換 */
                mPBLogin.setVisibility((logining) ? View.VISIBLE : View.INVISIBLE);
                mBtnLogin.setVisibility((logining) ? View.INVISIBLE : View.VISIBLE);
            }
        }, (logining) ? 0 : 2000);
    }

}
