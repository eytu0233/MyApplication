package edu.ncku.testapplication.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by NCKU on 2015/12/1.
 */
public class LibOpenTimeReaderTask extends AsyncTask<Void, Void, Map<String, List<String>>> {

    private static final String DEBUG_FLAG = LibOpenTimeReaderTask.class.getName();
    private static final String FILE_NAME = "NCKU_Lib_Open_Time";

    private Context mContext;

    public LibOpenTimeReaderTask(Context context){
        this.mContext = context;
    }


    @Override
    protected Map<String, List<String>> doInBackground(Void... params) {

        File inputFile = null;
        ObjectInputStream ois = null;
        Map<String, List<String>> serviceMap = null;

        try {
            inputFile = new File(mContext
                    .getFilesDir(), FILE_NAME);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
                return null;
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                serviceMap = (Map<String, List<String>>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceMap;
    }
}
