package edu.ncku.application.io.file;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashSet;

import edu.ncku.application.fragments.MessagerFragment;
import edu.ncku.application.model.Message;
import edu.ncku.application.util.PreferenceKeys;
import edu.ncku.application.util.adapter.ListMsgsAdapter;

/**
 *
 */
public class MsgsReaderTask extends AsyncTask<Void, Void, ListMsgsAdapter> {
    private static final String DEBUG_FLAG = MsgsReaderTask.class.getName();
    private static final String SUB_FILE_NAME = ".messages";

    private int show;
    private String fileName;

    private Context context;
    private Activity activity;
    private ListMsgsAdapter listViewAdapter;

    public MsgsReaderTask(MessagerFragment fragment, int show) {
        this.activity = fragment.getActivity();
        this.context = activity.getApplicationContext();
        String username = PreferenceManager
                .getDefaultSharedPreferences(context).getString(PreferenceKeys.USERNAME, "");
        this.fileName = username + SUB_FILE_NAME;
        this.show = show;
    }

    @Override
    protected ListMsgsAdapter doInBackground(Void... params) {
        LinkedHashSet<Message> readMessages = null;
        ObjectInputStream ois = null;
        File inputFile = null;

        try {
            inputFile = new File(context.getFilesDir(), fileName);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                readMessages = (LinkedHashSet<Message>) ois.readObject();
                Log.d(DEBUG_FLAG,
                        "Read msgs from file : " + readMessages.size());
                if (ois != null)
                    ois.close();
            }

            if (readMessages == null || readMessages.size() == 0) {
                return null;
            }

            listViewAdapter = new ListMsgsAdapter(activity, readMessages, show);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listViewAdapter;
    }
}
