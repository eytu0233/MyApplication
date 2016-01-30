package edu.ncku.application.io.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.ncku.application.model.ContactInfo;

/**
 * 此類別繼承JsonReceiveTask，用來處理聯絡資訊JSON資料的接收
 * 並將其存進SD卡之中(覆蓋)。
 */
public class ContactInfoReceiveTask extends JsonReceiveTask implements Runnable{

    private static final String DEBUG_FLAG = ContactInfoReceiveTask.class.getName();
    private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webOrganization&lan=cht";
    private static final String FILE_NAME = "NCKU_Lib_Contact_Info";

    public ContactInfoReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {

        ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();

        try {
            JSONArray jsonArray = new JSONArray(jsonRecieve(JSON_URL)); // 透過父類別方法jsonRecieve取得JSONArray物件

            /* 將資料從JSON物件當中取出 */
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                contactInfos.add(new ContactInfo(
                        json.getString("Division"),
                        json.getString("Phone"),
                        json.getString("Email")
                ));
            }

            saveFile(contactInfos, FILE_NAME); // 存進檔案之中

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}