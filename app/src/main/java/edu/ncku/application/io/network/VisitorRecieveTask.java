package edu.ncku.application.io.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.ncku.application.util.Preference;

/**
 * 在館人數更新背景工作
 */
public class VisitorRecieveTask implements Runnable {

    private static final String DEBUG_FLAG = VisitorRecieveTask.class.getName();
    private static final String VISITORS_URL = "http://140.116.209.94/app_inside.php";

    private Context mContext;
    private boolean isBackground;
    private boolean isOnce;
    
    public VisitorRecieveTask(Context context, boolean isBackground, boolean isOnce) {
        this.mContext = context;
        this.isBackground = isBackground;
        this.isOnce = isOnce;
    }

    @Override
    public void run() {
        try {
            ConnectivityManager connectivityManager = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

            /* 再次確認網路狀態 */
            if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                String visitors = "";
                try {
                    visitors = HttpClient.sendPost(VISITORS_URL, "").trim();
                }catch (Exception e){
                    e.printStackTrace();
                    visitors = "";
                }

                /* 如果回傳結果包含非數字則清空 */
                if(!TextUtils.isDigitsOnly(visitors)){
                    visitors = "";
                }

                Intent mIntent = new Intent();
                mIntent.setAction("android.intent.action.VISITORS_RECEIVER");
                mIntent.putExtra("visitors", visitors);
                Preference.setVisitor(mContext, visitors); // 這是避免App剛開啟時，在一分鐘的空窗期內不會立即顯示的解決方法
                Log.v(DEBUG_FLAG, "visitors : " + visitors);
                mContext.sendBroadcast(mIntent);

                /* 註冊一分鐘後的在館人數請求工作 */
                if(isBackground && !isOnce) {
                    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                    executor.schedule(new VisitorRecieveTask(mContext, true, false), 1, TimeUnit.MINUTES);
                    executor.shutdown();
                    Log.v(DEBUG_FLAG, "註冊一分鐘後的在館人數請求工作");
                }else{
                    Log.v(DEBUG_FLAG, "點擊刷新");
                }
            }else{ // 網路斷線
                if(isBackground) {
                    Log.d(DEBUG_FLAG, "網路斷線，取消註冊一分鐘後的在館人數請求工作");
                    Preference.setVisitor(mContext, ""); // 斷線時清空
                }else{
                    Intent mIntent = new Intent();
                    mIntent.setAction("android.intent.action.VISITORS_RECEIVER");
                    mContext.sendBroadcast(mIntent);
                }
            }
        } catch (Exception e) {
            /* 當發生例外時，一律對前景發出網路不通的訊息 */
            if(!isBackground) {
                Intent mIntent = new Intent();
                mIntent.setAction("android.intent.action.VISITORS_RECEIVER");
                mContext.sendBroadcast(mIntent);
            }
            e.printStackTrace();
        }
    }
}
