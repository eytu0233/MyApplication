package edu.ncku.application.io.file;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import edu.ncku.application.adapter.ListMsgsAdapter;
import edu.ncku.application.fragments.MessagerFragment;
import edu.ncku.application.model.Message;
import edu.ncku.application.util.PreferenceKeys;

/**
 *此AsyncTask類別將會在推播訊息頁面被打開時執行，從檔案讀取推播訊息資料
 */
public class MsgsReaderTask extends AsyncTask<Void, Void, ListMsgsAdapter> {
    private static final String DEBUG_FLAG = MsgsReaderTask.class.getName();
    private static final String SUB_FILE_NAME = ".messages";

    private String fileName;

    private Activity activity;
    private ListMsgsAdapter listViewAdapter;

    public MsgsReaderTask(MessagerFragment fragment) {
        this.activity = fragment.getActivity();
        String username = PreferenceManager
                .getDefaultSharedPreferences(activity).getString(PreferenceKeys.ACCOUNT, "");
        this.fileName = username + SUB_FILE_NAME; // 每個使用者(學號)都有各自的推播訊息檔案
    }

    @Override
    protected ListMsgsAdapter doInBackground(Void... params) {
        LinkedList<Message> readMessages = null;
        ObjectInputStream ois = null;
        File inputFile = null;

        try {
            inputFile = new File(activity.getFilesDir(), fileName);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                readMessages = (LinkedList<Message>) ois.readObject();

                for(Message msg : readMessages){
                    Log.d(DEBUG_FLAG, "Title : " + msg.getTitle());
                }

                Log.d(DEBUG_FLAG,
                        "Read msgs from file : " + readMessages.size());
                if (ois != null)
                    ois.close();
            }

            if (readMessages == null || readMessages.size() == 0) {
                return null;
            }

            listViewAdapter = new ListMsgsAdapter(activity, readMessages); // 將資料包成ListMsgsAdapter
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listViewAdapter;
    }
}
