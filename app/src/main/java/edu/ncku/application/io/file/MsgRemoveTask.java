package edu.ncku.application.io.file;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import edu.ncku.application.model.Message;
import edu.ncku.application.util.PreferenceKeys;

/**
 * Created by NCKU on 2016/4/19.
 * 此AsyncTask類別將會在使用者點擊多選刪除時執行，用來刪除在檔案裏面的推播訊息
 */
public class MsgRemoveTask extends AsyncTask<List<Integer>, Void, Void> {

    private static final String DEBUG_FLAG = MsgRemoveTask.class.getName();
    private static final String SUB_FILE_NAME = ".messages";

    private Context context;
    private String fileName;

    public MsgRemoveTask(Context context) {
        this.context = context;
        // 每個使用者(學號)都有各自的推播訊息檔案
        this.fileName = PreferenceManager
                .getDefaultSharedPreferences(context).getString(PreferenceKeys.USERNAME, "") + SUB_FILE_NAME;
    }

    @Override
    protected Void doInBackground(List<Integer>... params) {

        if(params.length != 1) {
            Log.e(DEBUG_FLAG, "params length is not 1");
            return null;
        }

        Log.d(DEBUG_FLAG, "List size : " + params[0].size()); // 要刪除的推播訊息用一個整數陣列(由小到大排序)

        LinkedList<Message> readMsgs = null, removeMsgs = new LinkedList<Message>();
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        File msgFile = null;

        try {
            msgFile = new File(context.getFilesDir(), fileName);

            if (!msgFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
            } else {
                ois = new ObjectInputStream(new FileInputStream(msgFile));
                readMsgs = (LinkedList<Message>) ois.readObject();
                if (ois != null)
                    ois.close();

                Log.d(DEBUG_FLAG, "剩下" + readMsgs.size()  + "個訊息");

                int counter = 0;
                for(int index : params[0]){
                    readMsgs.remove(index - counter); // 因為刪除後index會減1，故實際index為原本index - counter
                    counter++;
                    Log.d(DEBUG_FLAG, "remove " + index + " from sd");
                }

                Log.d(DEBUG_FLAG, "刪除完後剩下" + readMsgs.size() + "個訊息");

                /* 將刪除後的推播訊息寫回檔案 */
                oos = new ObjectOutputStream(new FileOutputStream(msgFile));
                oos.writeObject(readMsgs);
                oos.flush();
                oos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
