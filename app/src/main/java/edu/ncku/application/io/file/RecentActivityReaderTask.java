package edu.ncku.application.io.file;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import edu.ncku.application.util.EnvChecker;

/**
 * 此AsyncTask類別將會在近期活動頁面開啟時被執行，進行頁面資料讀取的工作
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
                    .getFilesDir(), FILE_NAME + ((EnvChecker.isLunarSetting())?"_cht":"_eng"));

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
