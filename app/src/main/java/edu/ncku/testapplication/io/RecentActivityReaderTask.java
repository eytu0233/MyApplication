package edu.ncku.testapplication.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Created by NCKU on 2015/11/27.
 */
public class RecentActivityReaderTask extends AsyncTask<Void, Void, Map<String, String>> {

    private static final String DEBUG_FLAG = RecentActivityReaderTask.class.getName();
    private static final String FILE_NAME = "NCKU_Lib_RecentActivity";

    private Context mContext;

    public RecentActivityReaderTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Map<String, String> doInBackground(Void... params) {
        File inputFile = null;
        ObjectInputStream ois = null;
        Map<String, String> imgSuperLink = null;

        try {
            inputFile = new File(mContext
                    .getFilesDir(), FILE_NAME);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
                return null;
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                imgSuperLink = (Map<String, String>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgSuperLink;
    }
}
