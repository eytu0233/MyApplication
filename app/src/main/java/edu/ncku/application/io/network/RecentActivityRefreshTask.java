package edu.ncku.application.io.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edu.ncku.application.util.EnvChecker;

/**
 * Created by NCKU on 2016/5/17.
 */
public class RecentActivityRefreshTask extends AsyncTask<Context, Void, Map<String, String>> {

    private static final String DEBUG_FLAG = RecentActivityRefreshTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webActivity&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String FILE_NAME = "NCKU_Lib_RecentActivity";

    @Override
    protected Map<String, String> doInBackground(Context... params) {

        if(params.length != 1) {
            Log.e(DEBUG_FLAG, "Arguments error");
            return null;
        }

        final Context context = params[0];

        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Map<String, String> imgSuperLink = new HashMap<String, String>();

        if (currentNetworkInfo == null || !currentNetworkInfo.isConnected()) {
            Log.w(DEBUG_FLAG, "網路尚未連線，無法更新最新消息");
            return imgSuperLink;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonRecieve(JSON_URL));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String imgUrl = json.getString("ImgUrl");
                String activityURL = json.getString("ActivityURL");

                imgSuperLink.put(imgUrl, activityURL);
            }

            saveFile(imgSuperLink, FILE_NAME, context);

        } catch (JSONException e) {
            Log.e(DEBUG_FLAG, "最近活動Json格式解析錯誤或沒有資料");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgSuperLink;
    }

    private final String jsonRecieve(final String jsonURL) {
        HttpURLConnection urlConnection = null;
        StringBuilder responseStrBuilder = null;

        try {
            URL url = new URL(jsonURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            Log.e(DEBUG_FLAG, "網頁連線逾時");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

        return responseStrBuilder.toString();
    }

    private final void saveFile(final Object data, final String fileName, final Context context) throws IOException, NullPointerException {
        if (data == null) {
            throw new NullPointerException("argument data is null.");
        }

        /* Get internal storage directory */
        File dir = context.getFilesDir();
        File activityFile = new File(dir, fileName);

        ObjectOutputStream oos = null;

        oos = new ObjectOutputStream(new FileOutputStream(activityFile));
        oos.writeObject(data);
        oos.flush();
        oos.close();
    }
}
