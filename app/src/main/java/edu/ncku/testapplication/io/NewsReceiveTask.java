package edu.ncku.testapplication.io;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.ncku.testapplication.data.News;

public class NewsReceiveTask extends JsonReceiveTask implements Runnable {

	private static final String DEBUG_FLAG = NewsReceiveTask.class.getName();
	private static final String FILE_NAME = "News";
	private static final String JSON_URL = "http://140.116.207.24/libweb/index.php?item=webNews&lan=cht";
	private static final Object LOCKER = new Object();

	private static NetworkInfo currentNetworkInfo;

	private boolean isOnce = false;
	private Context mContext;
	private Intent mIntent = new Intent();

	public NewsReceiveTask(Context mContext, boolean isOnce) {
		super(mContext);
		this.mContext = mContext;
		this.isOnce = isOnce;
		this.mIntent.setAction("android.intent.action.MY_RECEIVER");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (mContext != null) {
			ConnectivityManager connectivityManager = ((ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE));
			currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
		}

		if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {

			receiveNewsFromNetwork();

		} else {
			if (isOnce) {
				mIntent.putExtra("flag", "目前網路尚未開啟,無法更新訊息。");
				mContext.sendBroadcast(mIntent);
			}
		}
	}

	private int synMsgFile(LinkedList<News> newsList) {

		/* Get internal storage directory */
		File dir = mContext.getFilesDir();
		File newsFile = new File(dir, FILE_NAME);

		ObjectInputStream ois;
		ObjectOutputStream oos;
		LinkedHashSet<News> readNews, mergeReadNews;

		int updateNum = 0;

		synchronized (LOCKER) {
			try {
				// read news data from file
				if (newsFile.exists()) {
					ois = new ObjectInputStream(new FileInputStream(newsFile));
					readNews = (LinkedHashSet<News>) ois.readObject();
					if (ois != null)
						ois.close();
				} else{
					readNews = new LinkedHashSet<News>();
				}

				// record the old number of news
				int oldNum = readNews.size();
				// merges two news set to readNews
				mergeReadNews = new LinkedHashSet<News>();
				mergeReadNews.addAll(newsList);
				mergeReadNews.addAll(readNews);

				updateNum = mergeReadNews.size() - oldNum;

				// overwrite the news data to the file
				oos = new ObjectOutputStream(new FileOutputStream(newsFile));
				if (!isOnce) {
					deleteOutOfDateNews(mergeReadNews);
				}
				oos.writeObject(mergeReadNews);
				oos.flush();
				if (oos != null)
					oos.close();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Log.e(DEBUG_FLAG, "The read object can't be found.");
				newsList = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return updateNum;
	}

	private void deleteOutOfDateNews(LinkedHashSet<News> newsSet){
		final long ALIVE_DAYS = 90;
		final long SECONDS_OF_A_DAY = 24 * 60 * 60;
		final long OUT_OF_DATE_TIMESTAMP = ALIVE_DAYS * SECONDS_OF_A_DAY;
		long nowTimeStamp = System.currentTimeMillis() / 1000L;

		LinkedHashSet<News> deleteNewsSet = new LinkedHashSet<News>();

		for(News news : newsSet){
			if(nowTimeStamp - (long)news.getTimeStamp() >= OUT_OF_DATE_TIMESTAMP){
				deleteNewsSet.add(news);
			}
		}

		if(!deleteNewsSet.isEmpty()) newsSet.removeAll(deleteNewsSet);
	}

	private void receiveNewsFromNetwork() {
		LinkedList<News> news;

		int numNews = 0;

		try {

			JSONArray arr = new JSONArray(jsonRecieve(JSON_URL));

			news = new LinkedList<News>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String relatedLink = json.getString("related_url");
				String att_file_1 = json.getString("att_file_1");
				String att_file_1_des = json.getString("att_file_1_des");
				String att_file_2 = json.getString("att_file_2");
				String att_file_2_des = json.getString("att_file_2_des");
				String att_file_3 = json.getString("att_file_3");
				String att_file_3_des = json.getString("att_file_3_des");
				String content = json.getString("news_text");
				String contact_unit = json.getString("contact_unit");
				String contact_tel = json.getString("contact_tel");
				String contact_email = json.getString("contact_email");

				if (relatedLink.length() > 0) {
					content += "<br><tr><td class=\"newslink\"><img src=\"link.png\" height=\"20\" width=\"20\"><a href="
							+ relatedLink
							+ " target=\"_blank\" class=\"ui-link\">相關連結</a></td></tr><br>";
				}

				if (att_file_1.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_1
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_1_des.length() > 0) ? att_file_1_des
							: "相關附件1") + "</a></td></tr><br>";
				}

				if (att_file_2.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_2
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_2_des.length() > 0) ? att_file_2_des
							: "相關附件2") + "</a></td></tr><br>";
				}

				if (att_file_3.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_3
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_3_des.length() > 0) ? att_file_3_des
							: "相關附件3") + "</a></td></tr><br>";
				}

				if(contact_unit.length() > 0){
					content += "<br>"
							+ contact_unit;
				}

				if(contact_tel.length() > 0){
					content += "&nbsp;"
							+ contact_tel
							+ "<br>";
				}

				if(contact_email.length() > 0){
					content += "<br>"
							+ contact_email
							+ "<br>";
				}

				news.add(new News(json.getString("news_title"), json
						.getString("publish_dept"),
						json.getInt("publish_time"), content));
			}

			Log.d(DEBUG_FLAG, "get news from network : " + news.size());

			numNews = synMsgFile(news);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isOnce) {
			mIntent.putExtra("numNews", numNews);
			mIntent.putExtra("flag", "FinishFlushFlag");
			mContext.sendBroadcast(mIntent);
		}
	}
}