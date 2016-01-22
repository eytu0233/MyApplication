package edu.ncku.application.io.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NCKU on 2015/11/27.
 */
public class RecentActivityReceiveTask extends JsonReceiveTask implements Runnable {

    private static final String DEBUG_FLAG = RecentActivityReceiveTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webActivity&lan=cht";
    private static final String FILE_NAME = "NCKU_Lib_RecentActivity";

    public RecentActivityReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        Map<String, String> imgSuperLink = new HashMap<String, String>();

        try {
            JSONArray jsonArray = new JSONArray(jsonRecieve(JSON_URL));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String imgUrl = json.getString("ImgUrl");
                String activityURL = json.getString("ActivityURL");

                imgSuperLink.put(imgUrl, activityURL);
            }

            saveFile(imgSuperLink, FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
