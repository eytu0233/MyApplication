package edu.ncku.application.io.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.ncku.application.R;
import edu.ncku.application.model.News;
import edu.ncku.application.util.PreferenceKeys;

/**
 * 此類別用來在背景接收最新消息的JSON資料，一樣將其存進檔案之中
 */
public class NewsReceiveTask extends JsonReceiveTask implements Runnable {

	private static final String DEBUG_FLAG = NewsReceiveTask.class.getName();
	private static final String FILE_NAME = "News";
	private static final String NEWS_JSON_URL = "http://140.116.207.24/libweb/index.php?item=webNews&lan=cht";
	private static final Object LOCKER = new Object();

	private static NetworkInfo currentNetworkInfo;

	private boolean isOnce = false; // 判斷是否為使用者刷新最新消息頁面
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

		// 判斷網路是否連線
		if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {

			receiveNewsFromNetwork();
			if(!isOnce) { // 當為背景執行，定時(每小時)執行此工作直到網路關閉為止
				Executors.newSingleThreadScheduledExecutor().schedule(this, 1, TimeUnit.HOURS);
			}

		} else {
			/* 廣播給NewsFragment告知網路目前無法連線 */
			if (isOnce) {
				mIntent.putExtra("flag", mContext.getString(R.string.messenger_network_disconnected));
				mContext.sendBroadcast(mIntent);
			}
		}
	}

	/**
	 *  將最新消息資料存進檔案，但之中有重複的最新消息
	 *  不會多次存取只留一個。並且刪除超過保存時間的最新消息。
	 *
	 * @param newsList 來自網路的最新消息資料
	 * @return 新增的最新消息數量(假如都重複則為0)
	 */
	private int synNewsFile(LinkedList<News> newsList) {

		/* Get internal storage directory */
		File dir = mContext.getFilesDir();
		File newsFile = new File(dir, FILE_NAME);

		ObjectInputStream ois;
		ObjectOutputStream oos;
		LinkedHashSet<News> readNews/*, pushNews*/, mergeReadNews; // 宣告成有序集合(不重複且加入有順序)

		int updateNum = 0;

		synchronized (LOCKER) { // 為避免有可能的race condition 以同步化區塊框之
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

				/*pushNews = new LinkedHashSet<News>();
				for(News news : newsList){
					//if(!news.isPush() || readNews.contains(news)) continue; // 當該封最新消息(來自網路)無需通知或者是已經通知過了，則跳過

					pushNews.add(news);
				}*/

				// record the old number of news
				int oldNum = readNews.size();
				// merges two news set to readNews
				mergeReadNews = new LinkedHashSet<News>();
				mergeReadNews.addAll(newsList);
				mergeReadNews.addAll(readNews);

				updateNum = mergeReadNews.size() - oldNum;

				deleteOutOfDateNews(mergeReadNews); // 清除過期最新消息

				// overwrite the news data to the file
				oos = new ObjectOutputStream(new FileOutputStream(newsFile));
				oos.writeObject(mergeReadNews);
				oos.flush();
				if (oos != null)
					oos.close();

				/*if(pushNews != null && pushNews.size() > 0){ // 當有需要通知的訊息時，找出其在聯集後集合的順序
					for(News news : pushNews){
						int position = 0;
						for(News mergeNews : mergeReadNews){
							if(news == mergeNews) {
								sendNotification(news.getTitle(), position);
							}
							++position;
						}
					}
				}*/

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

	/**
	 *  刪除已超過保存時間的最新消息
	 *  保存時間依照使用者給予的設定
	 *
	 * @param newsSet
	 */
	private void deleteOutOfDateNews(LinkedHashSet<News> newsSet){
		final long ALIVE_DAYS = 90; // 存活天數
		final long SECONDS_OF_A_DAY = 24 * 60 * 60; // 一天秒數
		final long OUT_OF_DATE_TIMESTAMP = ALIVE_DAYS * SECONDS_OF_A_DAY; // 時間戳記的差值

		long nowTimeStamp = System.currentTimeMillis() / 1000L; // 取得當前時間戳記

		LinkedHashSet<News> deleteNewsSet = new LinkedHashSet<News>();

		for(News news : newsSet){
			if(nowTimeStamp - (long)news.getEndTime() >= OUT_OF_DATE_TIMESTAMP){
				deleteNewsSet.add(news);
			}
		}

		if(!deleteNewsSet.isEmpty()) newsSet.removeAll(deleteNewsSet); // 刪除過期最新消息
	}

	/**
	 * 從網路接收最新消息
	 */
	private void receiveNewsFromNetwork() {
		LinkedList<News> news;

		int numNews = 0;
		boolean pushFlag = false;

		try {

			JSONObject jsonObject = new JSONObject(jsonRecieve(NEWS_JSON_URL));
			String noDataMsg = jsonObject.getString("noDataMsg");
			Log.d(DEBUG_FLAG, "noDataMsg : " + noDataMsg);
			if(!noDataMsg.isEmpty()){
				PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(PreferenceKeys.NO_DATA_MSGS, noDataMsg).apply();
				Log.d(DEBUG_FLAG, "noDataMsg saving...");
			}
			JSONArray arr = jsonObject.getJSONArray("NewsList");

			news = new LinkedList<News>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String title = json.getString("news_title");
				String publish_dept = json.getString("publish_dept");
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
				//String push = json.getString("push");

				int publish_time = json.getInt("publish_time");
				int end_time = json.getInt("end_time");

				if (relatedLink != null && relatedLink.length() > 0) {
					content += "<br><tr><td class=\"newslink\"><img src=\"link.png\" height=\"20\" width=\"20\"><a href="
							+ relatedLink
							+ " target=\"_blank\" class=\"ui-link\">相關連結</a></td></tr><br>";
				}

				if (att_file_1 != null && att_file_1.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_1
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_1_des.length() > 0) ? att_file_1_des
							: "相關附件1") + "</a></td></tr><br>";
				}

				if (att_file_2 != null && att_file_2.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_2
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_2_des.length() > 0) ? att_file_2_des
							: "相關附件2") + "</a></td></tr><br>";
				}

				if (att_file_3 != null && att_file_3.length() > 0) {
					content += "<br><tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_3
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_3_des.length() > 0) ? att_file_3_des
							: "相關附件3") + "</a></td></tr><br>";
				}

				if(contact_unit != null && contact_unit.length() > 0){
					content += "<br>"
							+ contact_unit;
				}

				if(contact_tel != null && contact_tel.length() > 0){
					content += "&nbsp;"
							+ contact_tel
							+ "<br>";
				}

				if(contact_email != null && contact_email.length() > 0){
					content += "<br>"
							+ contact_email
							+ "<br>";
				}

				news.add(new News(title, publish_dept, publish_time, end_time, content));
			}

			Log.d(DEBUG_FLAG, "get news from network : " + news.size());

			numNews = synNewsFile(news);

		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 當使用者刷新最新消息頁面時，通知其更新頁面 */
		if (isOnce) {
			mIntent.putExtra("numNews", numNews);
			mIntent.putExtra("flag", "FinishFlushFlag");
			mContext.sendBroadcast(mIntent);
		}
	}

	/*private void sendNotification(String message, int position) {

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.putExtra(PreferenceKeys.NEWS_EXTRA, position);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //清除之前已開啟的Activity
		intent.setData(Uri.parse("custom://" + System.currentTimeMillis())); // 用來區分intent以避免PendingIntent覆蓋extra資料
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 , intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(mContext.getString(R.string.app_name))
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(position, notificationBuilder.build());
	}*/
}