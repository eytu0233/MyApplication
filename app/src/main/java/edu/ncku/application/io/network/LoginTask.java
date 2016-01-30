package edu.ncku.application.io.network;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import edu.ncku.application.util.ILoginResultListener;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 在背景執行登入驗證工作
 */
public class LoginTask extends AsyncTask<Map<String, String>, Void, Boolean> {
	
	private static final String DEBUG_FLAG = LoginTask.class.getName();

	private static final String ATHU_URL = "http://reader.lib.ncku.edu.tw/login/index.php";
	
	private ILoginResultListener resultListener;

	public LoginTask(ILoginResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	@Override
	protected Boolean doInBackground(Map<String, String>... params) {
		// TODO Auto-generated method stub
		Map<String, String> parametersMap;

		boolean result = false;

		if (params.length != 1) {
			return result;
		} else {
			parametersMap = params[0];
		}

		try {
			URL url = new URL(ATHU_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("POST");

			// Send post request
			urlConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					urlConnection.getOutputStream());
			
			// Set parameters ID and Password with url encode format
			wr.writeBytes(String.format("username=%s&password=%s", URLEncoder
					.encode(parametersMap.get(PreferenceKeys.USERNAME), "utf-8"),
					URLEncoder.encode(parametersMap.get(PreferenceKeys.PASSWORD),
							"utf-8")));

			wr.flush();
			wr.close();

			InputStream input = urlConnection.getInputStream();
			byte[] data = new byte[1024];
			int idx = input.read(data);
			String str = new String(data, 0, idx);
			if (str.contains("OK")) {
				result = true;
			}
			input.close();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
