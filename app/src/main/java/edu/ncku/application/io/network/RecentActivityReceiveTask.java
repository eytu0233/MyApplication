package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 此類別用來接收近期活動的JSON資料，一樣儲存進檔案
 */
public class RecentActivityReceiveTask extends JsonReceiveTask {

    private static final String DEBUG_FLAG = RecentActivityReceiveTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webActivity&lan=";
    private static final String FILE_NAME = "NCKU_Lib_RecentActivity";

    public RecentActivityReceiveTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        try {
            saveFile(decodeJson(JSON_URL + "cht"), FILE_NAME + "_cht");
            saveFile(decodeJson(JSON_URL + "eng"), FILE_NAME + "_eng");
        } catch (JSONException e) {
            Log.e(DEBUG_FLAG, "最近活動Json格式解析錯誤或沒有資料");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> decodeJson(String url) throws IOException, JSONException {
        Map<String, String> imgSuperLink = new HashMap<String, String>();

        JSONArray jsonArray = new JSONArray(jsonRecieve(url));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String imgUrl = json.getString("ImgUrl");
            String activityURL = json.getString("ActivityURL");

            imgSuperLink.put(imgUrl, activityURL);
        }

        return imgSuperLink;
    }
}
