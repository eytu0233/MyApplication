package edu.ncku.application.io.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import edu.ncku.application.service.RegistrationIntentService;
import edu.ncku.application.util.ILoginResultListener;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 在背景執行登入驗證工作
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {
	
	private static final String DEBUG_FLAG = LoginTask.class.getName();

	private static final String ATHU_URL = "http://reader.lib.ncku.edu.tw/login/index.php";
	private static final String SYB_SUB_URL = "http://140.116.207.24/push/subscriptionStatus.php";

	private Context context;
	private SharedPreferences sharedPreferences;
	private ILoginResultListener resultListener;


	public LoginTask(Context context, ILoginResultListener resultListener) {
		super();
		this.context = context;
		this.resultListener = resultListener;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub

		boolean result = false;

		if (params == null || params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
			return result;
		}

		String username =  params[0], password = params[1];

		try {
			String str = HttpClient.sendPost(ATHU_URL, String.format("username=%s&password=%s", username, password));
			if (str.contains("OK")) {
				result = true;

				// 如果發現DeviceID從未傳送到Server或被清除則重新從GCM Server取得DeviceID
				if(!sharedPreferences.getBoolean(PreferenceKeys.SENT_TOKEN_TO_SERVER, false)){
					RegistrationIntentService.subscribeAction(context);
				}

				/* 登入成功的同時，進行訂閱狀態的同步 */
				str = HttpClient.sendPost(SYB_SUB_URL, String.format("id=%s", username));
				if (str.contains("Y")) {
					sharedPreferences.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, true).apply();
					Log.d(DEBUG_FLAG, "同步訂閱狀態 : Y");
				} else if (str.contains("N")) {
					sharedPreferences.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, false).apply();
					Log.d(DEBUG_FLAG, "同步訂閱狀態 : N");
				} else {
					throw new Exception("SYN_SUB_URL Fail");
				}
			}

		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if(resultListener != null) resultListener.loginEvent(result);
		super.onPostExecute(result);
	}

	
}
