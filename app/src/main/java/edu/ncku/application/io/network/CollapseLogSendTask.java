package edu.ncku.application.io.network;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by NCKU on 2016/6/14.
 */
public class CollapseLogSendTask implements Runnable {

    private static final String DEBUG_FLAG = CollapseLogSendTask.class.getName();
    private static final String LOGS_URL = "http://m.lib.ncku.edu.tw/push/android_crash_logs.php";
    private static final String LOG_FILE = "CollapseLog";

    private Context mContext;

    public CollapseLogSendTask(Context context) {
        this.mContext = context;
    }

    @Override
    public void run() {
        File logFile = new File(mContext.getFilesDir(), LOG_FILE);
        if(!logFile.exists()) return;

        String log = "crashLog=", line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logFile));

            while ((line = reader.readLine()) != null){
                log += line + "\n";
            }

            HttpClient.sendPost(LOGS_URL, log);
            Log.d(DEBUG_FLAG, "send log...");

            logFile.delete();
            Log.d(DEBUG_FLAG, "delete log...");
        } catch (FileNotFoundException e) {
            Log.e(DEBUG_FLAG, "Collapse log file is not found.", e);
        }  catch (Exception e) {
            Log.e(DEBUG_FLAG, "Some exceptions", e);
        } finally {
            if(reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
