package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.ncku.application.io.IOConstatnt;

/**
 * 此類別用來接收近期活動的JSON資料，一樣儲存進檔案
 */
public class UpcomingEventsReceiveTask extends JsonReceiveTask implements IOConstatnt {

    private static final String DEBUG_FLAG = UpcomingEventsReceiveTask.class.getName();
    private static final String JSON_URL = UPCOMING_EVENT_URL;
    private static final String FILE_NAME = UPCOMING_EVENT_FILE;

    public UpcomingEventsReceiveTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        try {
            saveFile(decodeJson(JSON_URL + "cht"), FILE_NAME + "_cht");
            saveFile(decodeJson(JSON_URL + "eng"), FILE_NAME + "_eng");
        } catch (Exception e){
            Log.e(DEBUG_FLAG, "最近活動Json格式解析錯誤或沒有資料", e);
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
