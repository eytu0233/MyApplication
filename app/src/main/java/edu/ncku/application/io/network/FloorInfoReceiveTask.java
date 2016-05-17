package edu.ncku.application.io.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import edu.ncku.application.util.EnvChecker;

/**
 * 此類別繼承JsonReceiveTask，用來處理樓層資訊JSON資料的接收
 * 並將其存進SD卡之中(覆蓋)。
 */
public class FloorInfoReceiveTask extends JsonReceiveTask{

    private static final String DEBUG_FLAG = FloorInfoReceiveTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webFloorplan&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String FILE_NAME = "NCKU_Lib_Floor_Info";

    public FloorInfoReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        LinkedHashMap<String, String> floorInfo = new LinkedHashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonRecieve(JSON_URL));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String floor = json.getString("Floor");
                String introduction = json.getString("Introduction");

                floorInfo.put(floor, introduction);
            }

            saveFile(floorInfo, FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
