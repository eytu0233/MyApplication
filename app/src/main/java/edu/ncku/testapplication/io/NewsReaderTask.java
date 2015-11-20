package edu.ncku.testapplication.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedHashSet;

import edu.ncku.testapplication.data.News;
import edu.ncku.testapplication.fragments.NewsFragment;
import edu.ncku.testapplication.util.ListNewsAdapter;

public class NewsReaderTask extends AsyncTask<Void, Void, ListNewsAdapter>{

	private static final String DEBUG_FLAG = NewsReaderTask.class.getName();
	private static final String FILE_NAME = "News";
	
	private NewsFragment newsFragment;
	private Context context;
	private ListNewsAdapter listViewAdapter;

	private int show;

	public ListNewsAdapter getListViewAdpater() {
		return listViewAdapter;
	}

	public NewsReaderTask(NewsFragment newsFragment, int show) {
		// TODO Auto-generated constructor stub
		this.newsFragment = newsFragment;
		this.context = newsFragment.getActivity().getApplicationContext();
		this.show = show;
	}

	@Override
	protected ListNewsAdapter doInBackground(Void... params) {
		// TODO Auto-generated method stub
		LinkedHashSet<News> readNews = null;
		ObjectInputStream ois = null;
		File inputFile = null;

		try {
			inputFile = new File(context
					.getFilesDir(), FILE_NAME);

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

			listViewAdapter = new ListNewsAdapter(newsFragment.getActivity(), readNews, show);
			newsFragment.setListAdapter(listViewAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listViewAdapter;
	}
}
