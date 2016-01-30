package edu.ncku.application.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import edu.ncku.application.LoginDialog;
import edu.ncku.application.MainActivity;
import edu.ncku.application.fragments.HomePageFragment;
import edu.ncku.application.fragments.MessagerFragment;
import edu.ncku.application.fragments.ProgressFragment;
import edu.ncku.application.R;
import edu.ncku.application.util.adapter.DrawerListAdapter;

/**
 * Created by NCKU on 2016/1/12.
 * 為了減少DrawerList在狀態間的轉換所造成的hard code，
 * 故將DrawerList的狀態轉換透過此類別來進行。
 * 此外，透過抽象化類別DrawerListItem，同一點擊事件將
 * 具有同一實體，提高可維護性與效能。
 */
public class DrawerListSelector implements ListView.OnItemClickListener{

    private static final String DEBUG_FLAG = DrawerListSelector.class.getName();

    private final DrawerListAdapter loginDrawerListAdapter;
    private final DrawerListAdapter logoutDrawerListAdapter;

    private MainActivity activity;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FragmentManager fragmentManager;

    public DrawerListSelector(final MainActivity activity, final DrawerLayout mDrawerLayout, final ListView mDrawerList) {
        this.activity = activity;
        this.mDrawerLayout = mDrawerLayout;
        this.mDrawerList = mDrawerList;
        this.fragmentManager = activity.getFragmentManager();

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerList.setOnItemClickListener(this);

        /* 實作點擊事件物件 */
        DrawerListItem homePageAdapterItem = new DrawerListItem(activity.getResources().getString(R.string.home_page)) {

            private final HomePageFragment mHomePageFragment = HomePageFragment.newInstance();

            @Override
            public void onDrawerItemClick() {
                activity.setTitle(R.string.app_name);
                replaceFragment(mHomePageFragment);
            }
        };
        DrawerListItem loginAdapterItem = new DrawerListItem(activity.getResources().getString(R.string.login)) {
            @Override
            public void onDrawerItemClick() {
                (new LoginDialog(DrawerListSelector.this, activity.getApplicationContext())).show(activity.getFragmentManager(), "Dialog");
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        };
        DrawerListItem logoutAdapterItem = new DrawerListItem(activity.getResources().getString(R.string.logout)) {
            @Override
            public void onDrawerItemClick() {
                    final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

                    SP.edit().putString(PreferenceKeys.USERNAME.toString(), "").apply();
                    SP.edit().putString(PreferenceKeys.PASSWORD.toString(), "").apply();
                    SP.edit().putBoolean(PreferenceKeys.SUBSCRIPTION, false).apply();

                    DrawerListSelector.this.logoutState();
            }
        };
        DrawerListItem messengerAdapterItem = new DrawerListItem(activity.getResources().getString(R.string.messenger)) {
            @Override
            public void onDrawerItemClick() {
                replaceFragment(MessagerFragment.newInstance(getUserName()));
            }
        };
        DrawerListItem barcodeAdapterItem = new DrawerListItem(activity.getResources().getString(R.string.barcode_scanner)) {
            @Override
            public void onDrawerItemClick() {
                final ProgressFragment progressFragment = ProgressFragment.newInstance();

                clearBackStackFragment();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .add(R.id.content_frame, progressFragment).commit();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.popBackStack();
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.initiateScan();
                    }
                }, 500);
            }
        };

        /* 將點擊事件物件加進各狀態列表之中 */
        ArrayList<DrawerListItem> loginDrawerListItems = new ArrayList<DrawerListItem>(); // 登入狀態列表
        loginDrawerListItems.add(homePageAdapterItem);
        loginDrawerListItems.add(messengerAdapterItem);
        loginDrawerListItems.add(logoutAdapterItem);
        loginDrawerListItems.add(barcodeAdapterItem);

        ArrayList<DrawerListItem> logoutDrawerListItems = new ArrayList<DrawerListItem>(); // 登出狀態列表
        logoutDrawerListItems.add(homePageAdapterItem);
        logoutDrawerListItems.add(loginAdapterItem);
        logoutDrawerListItems.add(barcodeAdapterItem);

        /* 將狀態列表放進各Adapter之中，之後將透過Adapters來進行列表狀態轉換 */
        loginDrawerListAdapter = new DrawerListAdapter(activity, loginDrawerListItems);
        logoutDrawerListAdapter = new DrawerListAdapter(activity, logoutDrawerListItems);

        homePageAdapterItem.onDrawerItemClick();
    }

    /**
        * 轉換成登入狀態列表
        */
    public void loginState() {
        mDrawerList.setAdapter(loginDrawerListAdapter);
    }

    /**
        * 轉換成登出狀態列表
        */
    public void logoutState() {
        mDrawerList.setAdapter(logoutDrawerListAdapter);
    }

    /**
        * 進行頁面取代轉換
        * @param fragment 要轉換的頁面
        */
    private void replaceFragment(Fragment fragment){
        if (fragment != null) {
            clearBackStackFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }
    }

    /**
        * 清空頁面堆疊與標題堆疊
        */
    private void clearBackStackFragment() {
        // clear the back stack for fragmentManager
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }

        activity.clearTitleStack();
    }

    /**
        *
        * @return 使用者名稱
        */
    private String getUserName() {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(activity);
        return SP.getString(PreferenceKeys.USERNAME.toString(), "");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /* 透過當前的Adapter取得對應的點擊事件物件來呼叫實作的點擊事件 */
        ((DrawerListItem)mDrawerList.getAdapter().getItem(position)).onDrawerItemClick();
        mDrawerList.setItemChecked(position, true); // 將點擊目標設為該項物件
        mDrawerLayout.closeDrawer(mDrawerList); // 關閉DrawerList
    }

}
