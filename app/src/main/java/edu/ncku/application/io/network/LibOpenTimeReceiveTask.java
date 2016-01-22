package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NCKU on 2015/12/1.
 */
public class LibOpenTimeReceiveTask extends JsonReceiveTask implements Runnable {

    private static final String DEBUG_FLAG = LibOpenTimeReceiveTask.class.getName();
    private static final String JSON_MAIN_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMainLib&lan=cht";
    private static final String JSON_STUDY_HALL_URL = "http://140.116.207.24/libweb/index.php?item=webStudyHall&lan=cht";
    private static final String JSON_MED_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMedLib&lan=cht";
    private static final String JSON_DEPT_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webDeptLib&lan=cht";
    private static final String FILE_NAME = "NCKU_Lib_Open_Time";


    Map<String, ArrayList<String>> serviceMap = new HashMap<String, ArrayList<String>>();

    public LibOpenTimeReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        try {
            serviceMap.put("總圖書館", jsonParsingServicesMainLib(new JSONObject(jsonRecieve(JSON_MAIN_LIB_URL))));
            Log.d(DEBUG_FLAG, "總圖書館Json");
            serviceMap.put("自修閱覽室（K館）",
                    jsonParsingServicesStudyHall(new JSONObject(jsonRecieve(JSON_STUDY_HALL_URL))));
            Log.d(DEBUG_FLAG, "自修閱覽室（K館Json");
            serviceMap.put("醫學院圖書分館",
                    jsonParsingServicesMedLib(new JSONObject(jsonRecieve(JSON_MED_LIB_URL))));
            Log.d(DEBUG_FLAG, "醫學院圖書分館Json");
            serviceMap.put("各系所及院圖",
                    jsonParsingServicesDeptLib(new JSONArray(jsonRecieve(JSON_DEPT_LIB_URL))));
            Log.d(DEBUG_FLAG, "各系所及院圖Json");

            saveFile(serviceMap, FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> jsonParsingServicesDeptLib(JSONArray jsonArray) throws Exception {

        final String SEMESTER = "學期中", VOCATION = "寒暑假";
        String semester = SEMESTER + "\n\n", vacation = VOCATION + "\n\n";

        ArrayList<String> services = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject service = jsonArray.getJSONObject(i);
            if(SEMESTER.equals(service.getString("service"))){
                semester += service.getString("dept") + "\n";
                if(!service.getString("note").isEmpty()){
                    semester += service.getString("note") + "\n";
                }
                JSONArray serviceLists = service.getJSONArray("ServiceList");
                for (int j = 0; j < serviceLists.length(); j++) {
                    JSONObject serviceList = serviceLists.getJSONObject(j);
                    semester += serviceList.getString("week") + " " + serviceList.getString("hours") + "\n\n";
                }
            }else if(VOCATION.equals(service.getString("service"))){
                vacation += service.getString("dept") + "\n";
                if(!service.getString("note").isEmpty()){
                    vacation += service.getString("note") + "\n";
                }
                JSONArray serviceLists = service.getJSONArray("ServiceList");
                for (int j = 0; j < serviceLists.length(); j++) {
                    JSONObject serviceList = serviceLists.getJSONObject(j);
                    vacation += serviceList.getString("week") + " " + serviceList.getString("hours") + "\n\n";
                }
            }
        }
        services.add(semester);
        services.add(vacation);

        return services;

    }

    private ArrayList<String> jsonParsingServicesMainLib(JSONObject jsonObject) throws Exception {

        ArrayList<String> services = new ArrayList<String>();

        JSONArray jsonArray = jsonObject.getJSONArray("ServiceResult");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray ServiceLists = jsonArray.getJSONObject(i).getJSONArray("ServiceList");
            String service = jsonArray.getJSONObject(i).getString("service");
            String serviceTime = "";
            for (int j = 0; j < ServiceLists.length(); j++) {
                JSONObject serviceList = ServiceLists.getJSONObject(j);
                serviceTime += serviceList.getString("week") + " " + serviceList.getString("hours") + "\n\n";
            }
            services.add(((service != null) ? service + "\n" : "") + serviceTime);
        }


        return services;

    }

    private ArrayList<String> jsonParsingServicesStudyHall(JSONObject jsonObject) throws Exception {

        String notes = "";
        ArrayList<String> services = new ArrayList<String>();

        JSONArray serviceLists = jsonObject.getJSONArray("ServiceList");
        for (int i = 0; i < serviceLists.length(); i++) {
            JSONObject serviceList = serviceLists.getJSONObject(i);
            notes += serviceList.getString("note") + "\n\n";
        }
        services.add(notes);

        return services;

    }

    private ArrayList<String> jsonParsingServicesMedLib(JSONObject jsonObject) throws Exception {
        String serviceTime = "";
        ArrayList<String> services = new ArrayList<String>();

        JSONArray serviceLists = jsonObject.getJSONArray("ServiceList");
        for (int i = 0; i < serviceLists.length(); i++) {
            JSONObject serviceList = serviceLists.getJSONObject(i);
            serviceTime += serviceList.getString("week") + " " + serviceList.getString("hours") + "\n\n";
        }
        services.add(serviceTime);

        return services;
    }

}
