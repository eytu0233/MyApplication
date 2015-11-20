package edu.ncku.testapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import edu.ncku.testapplication.io.LoginTask;
import edu.ncku.testapplication.util.ILoginResultListener;

public class LoginDialog extends DialogFragment {

	private static final String DEBUG_FLAG = LoginDialog.class.getName();

	private MainActivity mainActivity;
	private Context context;
	private Button mBtnLogin, mBtnCancel;
	private EditText mEditUsername, mEditPassword;
	private TextView mTxtTip;
	private ProgressBar mPBLogin;
	
	private boolean runningLogin = false;
	
	private synchronized boolean isRunningLogin(){
		return runningLogin;
	}
	
	private synchronized void setRunningLogin(boolean runningLogin){
		this.runningLogin = runningLogin;
	}

	public LoginDialog(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.context = mainActivity.getApplicationContext();
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

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		return builder.setView(v).create();
	}

	private void setEventListenner() {
		// TODO Auto-generated method stub
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setRunningLogin(true);
				mPBLogin.setVisibility(View.VISIBLE);
				mBtnLogin.setVisibility(View.INVISIBLE);

				final String username = mEditUsername.getText().toString(), password = mEditPassword
						.getText().toString();
				ConnectivityManager CM = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();

				if ((username != null && "".equals(username))
						|| (password != null && "".equals(password))) {
					mTxtTip.setText(R.string.void_account_or_password);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					setRunningLogin(false);
					return;
				}

				if (info == null || !info.isConnected()) {
					mTxtTip.setText(R.string.network_disconnected);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					setRunningLogin(false);
					return;
				}			
				
				final Map<String, String> params = new HashMap<String, String>();
				params.put("username", username);
				params.put("password", password);

				LoginTask loginTask = new LoginTask(new ILoginResultListener(){

					@Override
					public void loginEvent(boolean login) {
						// TODO Auto-generated method stub
						mPBLogin.setVisibility(View.INVISIBLE);
						mBtnLogin.setVisibility(View.VISIBLE);
						if (login) {
							mainActivity.changeNavigationLoginList();
							/*mainActivity.startMessagerService(); */
							final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				    		final SharedPreferences.Editor SPE = SP.edit();
				    		
				    		SPE.putString("username", username);
				    		SPE.putString("password", password);
				    		SPE.apply();
						} else {
							mTxtTip.setText(R.string.invalid_account_or_password);
						}
						
						setRunningLogin(false);
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
				onStop();
			}

		});
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if(isRunningLogin()) return;
		Log.d(DEBUG_FLAG, "onStop");
		super.onStop();
	}

}
