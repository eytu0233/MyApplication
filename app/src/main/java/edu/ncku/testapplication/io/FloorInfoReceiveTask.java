package edu.ncku.testapplication.io;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by NCKU on 2015/12/1.
 */
public class FloorInfoReceiveTask extends JsonReceiveTask implements Runnable{

    private static final String DEBUG_FLAG = FloorInfoReceiveTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/floorplan_json.php?item=webFloorplan";
    private static final String FILE_NAME = "NCKU_Lib_Floor_Info";

    public FloorInfoReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        LinkedHashMap<String, String> floorInfo = new LinkedHashMap<String, String>();

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
