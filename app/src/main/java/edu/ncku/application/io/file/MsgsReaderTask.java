package edu.ncku.application.io.file;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashSet;

import edu.ncku.application.model.News;
import edu.ncku.application.fragments.MessagerFragment;
import edu.ncku.application.util.adapter.ListMsgsAdapter;

/**
 *
 */
public class MsgsReaderTask extends AsyncTask<Void, Void, ListMsgsAdapter> {
    private static final String DEBUG_FLAG = NewsReaderTask.class.getName();

    private String fileName;
    private int show;

    private Activity activity;
    private ListMsgsAdapter listViewAdapter;

    public MsgsReaderTask(MessagerFragment fragment, String userName, int show) {
        this.activity = fragment.getActivity();
        this.fileName = userName;
        this.show = show;
    }

    @Override
    protected ListMsgsAdapter doInBackground(Void... params) {
        LinkedHashSet<News> readNews = null;
        ObjectInputStream ois = null;
        File inputFile = null;

        try {
            inputFile = new File(activity.getApplicationContext()
                    .getFilesDir(), fileName);

            if (!inputFile.exists()) {
                Log.d(DEBUG_FLAG, "file is not exist.");
            } else {
                ois = new ObjectInputStream(new FileInputStream(inputFile));
                readNews = (LinkedHashSet<News>) ois.readObject();
                Log.v(DEBUG_FLAG,
                        "Read msgs from file : " + readNews.size());
                if (ois != null)
                    ois.close();
            }

            if (readNews == null || readNews.size() == 0) {
                return null;
            }

            listViewAdapter = new ListMsgsAdapter(activity, readNews, show);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listViewAdapter;
    }
}
