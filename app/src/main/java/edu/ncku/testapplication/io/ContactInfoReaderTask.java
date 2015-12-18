package edu.ncku.testapplication.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import edu.ncku.testapplication.data.ContactInfo;

/**
 * Created by NCKU on 2015/12/8.
 */
public class ContactInfoReaderTask extends AsyncTask<Void, Void, ArrayList<ContactInfo>> {

    private static final String DEBUG_FLAG = ContactInfoReaderTask.class.getName();
    private static final String FILE_NAME = "NCKU_Lib_Contact_Info";

    private Context mContext;

    public ContactInfoReaderTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected ArrayList<ContactInfo> doInBackground(Void... params) {
        File inputFile = null;
        ObjectInputStream ois = null;
        ArrayList<ContactInfo> contactInfos = null;

        try {
            inputFile = new File(mContext
                    .getFilesDir(), FILE_NAME);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
                return null;
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                contactInfos = (ArrayList<ContactInfo>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactInfos;
    }
}
