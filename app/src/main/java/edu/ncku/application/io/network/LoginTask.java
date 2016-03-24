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

	private static final String ATHU_URL = "http://140.116.207.24/push/login.php";

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

		// 檢查參數值
		if (params == null || params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
			return result;
		}

		String username =  params[0], password = params[1];

		try {
			String str = HttpClient.sendPost(ATHU_URL, String.format("username=%s&password=%s", username, password));
			if (str.contains("OK")) {
				result = true;

				// 如果發現DeviceID從未傳送到Server或被清除則重新從GCM Server取得DeviceID
				String deviceID = sharedPreferences.getString(PreferenceKeys.DEVICE_TOKEN, "");
				if(deviceID == null || deviceID.equals("")){
					Log.d(DEBUG_FLAG, "登入時發現沒有註冊GCM，故啟動註冊背景服務");
					RegistrationIntentService.startActionRegisterGCM(context);
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
