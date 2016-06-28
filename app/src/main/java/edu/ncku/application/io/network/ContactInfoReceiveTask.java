package edu.ncku.application.io.network;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.ncku.application.io.IOConstatnt;
import edu.ncku.application.model.ContactInfo;

/**
 * 此類別繼承JsonReceiveTask，用來處理聯絡資訊JSON資料的接收
 * 並將其存進SD卡之中(覆蓋)。
 */
public class ContactInfoReceiveTask extends JsonReceiveTask implements IOConstatnt {

    private static final String DEBUG_FLAG = ContactInfoReceiveTask.class.getName();
    private static final String JSON_URL = CONTACT_URL;
    private static final String FILE_NAME = CONTACT_FILE;

    public ContactInfoReceiveTask(Context mContext) {
        super(mContext);
    }

    @Override
    public void run() {
        try {
            saveFile(decodeJson(JSON_URL + "cht"), FILE_NAME + "_cht"); // 存進檔案之中(中文)
            saveFile(decodeJson(JSON_URL + "eng"), FILE_NAME + "_eng"); // 存進檔案之中(英文)
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解碼JSON
     *
     * @param url JSON網站URL
     * @return 解碼後的聯絡資料
     * @throws IOException
     * @throws JSONException
     */
    private ArrayList<ContactInfo> decodeJson(String url) throws IOException, JSONException {
        ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();

        JSONArray jsonArray = new JSONArray(jsonRecieve(url));

        /* 將資料從JSON物件當中取出 */
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);

            contactInfos.add(new ContactInfo(
                    json.getString("Division"),
                    json.getString("Phone"),
                    json.getString("Email")
            ));
        }

        return contactInfos;
    }

}