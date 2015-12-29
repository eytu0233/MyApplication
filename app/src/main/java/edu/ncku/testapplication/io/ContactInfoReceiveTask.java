package edu.ncku.testapplication.io;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.ncku.testapplication.data.ContactInfo;

/**
 * Created by NCKU on 2015/12/1.
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
            JSONArray jsonArray = new JSONArray(jsonRecieve(JSON_URL));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                contactInfos.add(new ContactInfo(
                        json.getString("Division"),
                        json.getString("Phone"),
                        json.getString("Email")
                ));
            }

            saveFile(contactInfos, FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}