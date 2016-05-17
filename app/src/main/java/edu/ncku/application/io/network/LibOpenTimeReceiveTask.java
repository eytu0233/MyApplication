package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ncku.application.R;
import edu.ncku.application.util.EnvChecker;

/**
 * 此類別用來接收開館時間的JSON資料，一樣儲存進檔案
 */
public class LibOpenTimeReceiveTask extends JsonReceiveTask {

    private static final String DEBUG_FLAG = LibOpenTimeReceiveTask.class.getName();
    private static final String JSON_MAIN_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMainLib&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String JSON_STUDY_HALL_URL = "http://140.116.207.24/libweb/index.php?item=webStudyHall&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String JSON_MED_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMedLib&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String JSON_DEPT_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webDeptLib&lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng");
    private static final String FILE_NAME = "NCKU_Lib_Open_Time";


    Map<String, ArrayList<String>> serviceMap = new HashMap<String, ArrayList<String>>();

    public LibOpenTimeReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        try {
            String[] title = mContext.getResources().getStringArray(
                    R.array.lib_open_time_unit_list);
            serviceMap.put(title[0], jsonParsingServicesMainLib(new JSONObject(jsonRecieve(JSON_MAIN_LIB_URL))));
            Log.d(DEBUG_FLAG, title[0] + " Json");
            serviceMap.put(title[1],
                    jsonParsingServicesStudyHall(new JSONObject(jsonRecieve(JSON_STUDY_HALL_URL))));
            Log.d(DEBUG_FLAG, title[1] + " Json");
            serviceMap.put(title[2],
                    jsonParsingServicesMedLib(new JSONObject(jsonRecieve(JSON_MED_LIB_URL))));
            Log.d(DEBUG_FLAG, title[2] + " Json");
            serviceMap.put(title[3],
                    jsonParsingServicesDeptLib(new JSONArray(jsonRecieve(JSON_DEPT_LIB_URL))));
            Log.d(DEBUG_FLAG, title[3] + " Json");

            saveFile(serviceMap, FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
        * 取得各系所圖書館開放時間資料
        *
        * @param jsonArray 從網址中取得的JSON陣列物件
        * @return 排版過的字串List
        * @throws Exception
        */
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

    /**
     * 取得主圖書館開放時間資料
     *
     * @param jsonObject 從網址中取得的JSON物件
     * @return 排版過的字串List
     * @throws Exception
     */
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

    /**
     * 取得閱覽室開放時間資料
     *
     * @param jsonObject 從網址中取得的JSON物件
     * @return 排版過的字串List
     * @throws Exception
     */
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

    /**
     * 取得醫分館開放時間資料
     *
     * @param jsonObject 從網址中取得的JSON物件
     * @return 排版過的字串List
     * @throws Exception
     */
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
