package edu.ncku.application.io.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import edu.ncku.application.service.RegistrationIntentService;
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
	private SharedPreferences sharedPreferences;


	public LoginTask(Context context) {
		super();
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

        String result = "";

		// 檢查參數值
		if (params == null || params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
			return result;
		}

		String username =  params[0], password = params[1];

		try {
			Log.d(DEBUG_FLAG, "username : " + username);
			Log.d(DEBUG_FLAG, "password : " + (new Security()).encrypt(password));
			String str = HttpClient.sendPost(LOGIN_URL, String.format("username=%s&password=%s", username, URLEncoder.encode((new Security()).encrypt(password)), "UTF-8"));
			JSONObject json = new JSONObject(str);

            if(OK.equals(json.getString(RESULT_LABEL))){
                result = json.getString(NAME_LABEL);
                Log.d(DEBUG_FLAG, "Name : " + result);
                
                // 如果發現DeviceID從未傳送到Server或被清除則重新從GCM Server取得DeviceID
                String deviceID = sharedPreferences.getString(PreferenceKeys.DEVICE_TOKEN, "");
                if(deviceID == null || deviceID.equals("")){
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
	
}
