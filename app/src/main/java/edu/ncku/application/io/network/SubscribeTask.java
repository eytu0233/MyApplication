package edu.ncku.application.io.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import edu.ncku.application.util.PreferenceKeys;

/**
 * 此AsyncTask類別是用來讓圖書館伺服器的資料庫改變訂閱狀態，透過php網頁傳送參數
 */
public class SubscribeTask extends AsyncTask<Boolean, Void, Boolean> {

    private static final String DEBUG_FLAG = SubscribeTask.class.getName();

    private static final String SUB_URL = "http://m.lib.ncku.edu.tw/push/subscription.php";

    private Context mContext;

    public SubscribeTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        try {
            String username = sp.getString(PreferenceKeys.ACCOUNT, "");
            String did = sp.getString(PreferenceKeys.DEVICE_TOKEN, "");
            if(username != null && did != null && !username.isEmpty() && !did.isEmpty()){ // 避免因狀態改變的自動登出導致抓不到username
                if(!HttpClient.sendPost(SUB_URL, String.format("id=%s&did=%s&os=A&sub=%d", username, did, (params[0])?1:0)).contains("OK")) throw new Exception("Posting data to server failed");
                return true;
            }else{
                throw new Exception("Username or deviceID is wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
