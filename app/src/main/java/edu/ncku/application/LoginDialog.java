package edu.ncku.application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import edu.ncku.application.io.network.LoginTask;
import edu.ncku.application.util.DrawerListSelector;
import edu.ncku.application.util.ILoginResultListener;
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
	
	private boolean runningLogin = false;

	private ProgressDialog progressDialog;

	/**
	 *  這個Receiver會接收來自LoginTask的廣播
	 */
	private BroadcastReceiver mLoginTaskReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			/* 關閉progressDialog */
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					Toast.makeText(context, R.string.sub_handled, Toast.LENGTH_SHORT).show();
				}
			}, 1000);
		}
	};

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
				setRunningLogin(true); // 將狀態變成登入中
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
					mTxtTip.setText(R.string.void_account_or_password);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					setRunningLogin(false);
					return;
				}

				/* 判斷網路是否有連線 */
				if (info == null || !info.isConnected()) {
					mTxtTip.setText(R.string.network_disconnected);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					setRunningLogin(false);
					return;
				}

				/* 將填好的帳好跟密碼放進參數中 */
				final Map<String, String> params = new HashMap<String, String>();
				params.put(PreferenceKeys.USERNAME.toString(), username);
				params.put(PreferenceKeys.PASSWORD.toString(), password);

				/* 實作LoginTask中的ILoginResult介面 */
				LoginTask loginTask = new LoginTask(new ILoginResultListener() {

					@Override
					public void loginEvent(final boolean isLogin) {
						// TODO Auto-generated method stub
						/* 完成登入工作後，會藉由此方法取得結果 */
						(new Handler()).postDelayed(new Runnable() {
							@Override
							public void run() {
							/* 結束登入工作 */
							mPBLogin.setVisibility(View.INVISIBLE);
							mBtnLogin.setVisibility(View.VISIBLE);

							/* 登入成功 */
							if (isLogin) {
								drawerListSelector.loginState(); // 透過drawerListSelector來改變drawer狀態
								/* 將帳號密碼存進設定值 */
								final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
								SP.edit().putString(PreferenceKeys.USERNAME, username).apply();
								SP.edit().putString(PreferenceKeys.PASSWORD, password).apply();

								Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
							} else {
								mTxtTip.setText(R.string.invalid_account_or_password);
							}

							setRunningLogin(false);
							LoginDialog.this.dismiss();

							}
						}, 1000);
					}

				});
				loginTask.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, params);
			}

		});

		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isRunningLogin()) return;
				dismiss();
			}

		});
	}

	/**
	 *
	 * @return 當前是否處在登入中的狀態
	 */
	private synchronized boolean isRunningLogin(){
		return runningLogin;
	}

	/**
	 *
	 * @param runningLogin 設定當前是否處在登入中的狀態
	 */
	private synchronized void setRunningLogin(boolean runningLogin){
		this.runningLogin = runningLogin;
	}

}
