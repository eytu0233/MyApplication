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
import edu.ncku.testapplication.R;

/**
 * Created by NCKU on 2016/1/12.
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
                    final SharedPreferences.Editor SPE = SP.edit();

                    SPE.putString("username", "");
                    SPE.putString("password", "");
                    SPE.apply();

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

        ArrayList<DrawerListItem> loginDrawerListItems = new ArrayList<DrawerListItem>();
        loginDrawerListItems.add(homePageAdapterItem);
        loginDrawerListItems.add(messengerAdapterItem);
        loginDrawerListItems.add(logoutAdapterItem);
        loginDrawerListItems.add(barcodeAdapterItem);

        ArrayList<DrawerListItem> logoutDrawerListItems = new ArrayList<DrawerListItem>();
        logoutDrawerListItems.add(homePageAdapterItem);
        logoutDrawerListItems.add(loginAdapterItem);
        logoutDrawerListItems.add(barcodeAdapterItem);

        loginDrawerListAdapter = new DrawerListAdapter(activity, loginDrawerListItems);
        logoutDrawerListAdapter = new DrawerListAdapter(activity, logoutDrawerListItems);

        homePageAdapterItem.onDrawerItemClick();
    }

    public void loginState() {
        mDrawerList.setAdapter(loginDrawerListAdapter);
    }

    public void logoutState() {
        mDrawerList.setAdapter(logoutDrawerListAdapter);
    }

    private void replaceFragment(Fragment fragment){
        if (fragment != null) {
            clearBackStackFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }
    }

    private void clearBackStackFragment() {
        // clear the back stack for fragmentManager
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }

        activity.clearTitleStack();
    }

    private String getUserName() {
        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(activity);
        return SP.getString(PreferenceKeys.USERNAME.toString(), "");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((DrawerListItem)mDrawerList.getAdapter().getItem(position)).onDrawerItemClick();
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

}
