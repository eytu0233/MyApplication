package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ncku.application.R;

/**
 * 此類別用來接收開館時間的JSON資料，一樣儲存進檔案
 */
public class LibOpenTimeReceiveTask extends JsonReceiveTask {

    private static final String DEBUG_FLAG = LibOpenTimeReceiveTask.class.getName();
    private static final String JSON_MAIN_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMainLib&lan=";
    private static final String JSON_STUDY_HALL_URL = "http://140.116.207.24/libweb/index.php?item=webStudyHall&lan=";
    private static final String JSON_LEARN_COMM_URL = "http://140.116.207.24/libweb/index.php?item=webLearnComm&lan=";
    private static final String JSON_MED_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webMedLib&lan=";
    private static final String JSON_DEPT_LIB_URL = "http://140.116.207.24/libweb/index.php?item=webDeptLib&lan=";
    private static final String FILE_NAME = "NCKU_Lib_Open_Time";

    public LibOpenTimeReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        try {
            String[] title_cht = {
                    "總圖書館",
                    "自修閱覽室（K館）",
                    "學習開放空間(敬三)",
                    "醫學院圖書分館",
                    "各系所及院圖"
            };

            String[] title_eng = {
                    "Main Library",
                    "Study Hall",
                    "Learning Commons(Ching-Yen campus)",
                    "Medical Library",
                    "Department Libraries"
            };

            saveFile(decodeAllJson(title_cht, "cht"), FILE_NAME + "_cht");
            saveFile(decodeAllJson(title_eng, "eng"), FILE_NAME + "_eng");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, ArrayList<String>> decodeAllJson(String[] titles, String locale) throws Exception {
        Map<String, ArrayList<String>> serviceMap = new HashMap<String, ArrayList<String>>();

        serviceMap.put(titles[0], jsonParsingMainLib(new JSONObject(jsonRecieve(JSON_MAIN_LIB_URL + locale))));
        Log.d(DEBUG_FLAG, titles[0] + " Json");

        serviceMap.put(titles[1],
                jsonParsingStudyHall(new JSONObject(jsonRecieve(JSON_STUDY_HALL_URL + locale))));
        Log.d(DEBUG_FLAG, titles[1] + " Json");

        serviceMap.put(titles[2],
                jsonParsingLearnComm(new JSONObject(jsonRecieve(JSON_LEARN_COMM_URL + locale))));
        Log.d(DEBUG_FLAG, titles[2] + " Json");

        serviceMap.put(titles[3],
                jsonParsingMedLib(new JSONObject(jsonRecieve(JSON_MED_LIB_URL + locale))));
        Log.d(DEBUG_FLAG, titles[3] + " Json");

        serviceMap.put(titles[4],
                jsonParsingDeptLib(new JSONArray(jsonRecieve(JSON_DEPT_LIB_URL + locale))));
        Log.d(DEBUG_FLAG, titles[4] + " Json");

        return serviceMap;
    }

    /**
        * 取得各系所圖書館開放時間資料
        *
        * @param jsonArray 從網址中取得的JSON陣列物件
        * @return 排版過的字串List
        * @throws Exception
        */
    private ArrayList<String> jsonParsingDeptLib(JSONArray jsonArray) throws Exception {

        final String SEMESTER = mContext.getString(R.string.termTime), VACATION = mContext.getString(R.string.vacations);
        String semester = SEMESTER + "\n\n", vacation = VACATION + "\n\n";

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
            }else if(VACATION.equals(service.getString("service"))){
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
    private ArrayList<String> jsonParsingMainLib(JSONObject jsonObject) throws Exception {

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

    private ArrayList<String> jsonParsingLearnComm(JSONObject jsonObject) throws Exception {
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
     * 取得閱覽室開放時間資料
     *
     * @param jsonObject 從網址中取得的JSON物件
     * @return 排版過的字串List
     * @throws Exception
     */
    private ArrayList<String> jsonParsingStudyHall(JSONObject jsonObject) throws Exception {

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
    private ArrayList<String> jsonParsingMedLib(JSONObject jsonObject) throws Exception {
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
