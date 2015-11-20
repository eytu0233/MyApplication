package edu.ncku.testapplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.LinkedList;

import edu.ncku.testapplication.fragments.HomePageFragment;
import edu.ncku.testapplication.fragments.PrefFragment;
import edu.ncku.testapplication.service.NewsRecieveService;
import edu.ncku.testapplication.util.IReceiverRegisterListener;
import edu.ncku.testapplication.util.ITitleChangeListener;

public class MainActivity extends AppCompatActivity implements ITitleChangeListener, IReceiverRegisterListener {

    private static final String DEBUG_FLAG = MainActivity.class.getName();

    private LinkedList<String> titleStack = new LinkedList<String>();

    private CharSequence mTitle;
    private String[] mNavigationNormalList;
    private String[] mNavigationLoginList;

    private Fragment mHomePageFragment;
    private Fragment mSettingFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        initComponent();

        replaceFragment(mHomePageFragment);

        if(!isServiceRunning(NewsRecieveService.class)) {
            NewsRecieveService.startActionNORMAL(getApplicationContext());
        }

        /*String parameters = getIntent().getStringExtra("mainActivityParameter");

        if (parameters == null) {
            selectItem(0);
        }else if("Message Notification".equals(parameters)){
            selectItem((isLogin())?1:0);
        }else{
            Log.e(DEBUG_FLAG, "parameters undefined!");
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void replaceFragment(Fragment fragment){
        if(fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }
    }

    private void initUI() {

        mTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.imageViewLayout:
                        getFragmentManager().beginTransaction().addToBackStack(null)
                                .addToBackStack(null).add(R.id.content_frame, mSettingFragment).commit();
                        setTitle(R.string.setting);
                        return true;
                }
                return true;
            }
        };
        Drawable logo =  ContextCompat.getDrawable(this, R.drawable.ic_launcher);
        toolbar.setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null)
                if (child.getClass() == ImageView.class) {
                    ImageView logoImageView = (ImageView) child;
                    if ( logoImageView.getDrawable() == logo ) {
                        logoImageView.setAdjustViewBounds(true);
                        logoImageView.setPadding(0, 0, 30, 0);
                    }
                }
        }
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mNavigationNormalList = getResources().getStringArray(
                R.array.Navigation_normal_array);
        mNavigationLoginList = getResources().getStringArray(
                R.array.Navigation_login_array);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, (isLogin()) ? mNavigationLoginList : mNavigationNormalList));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
                                               @Override
                                               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                   selectItem(position);
                                               }
                                           }
        );

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                toolbar,
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initComponent() {

        mHomePageFragment = HomePageFragment.newInstance();
        //mMsgListFragment = new MessageListFragment(getApplicationContext());
        mSettingFragment = new PrefFragment();

    }

    private boolean isLogin() {

        final SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String username = SP.getString("username", ""), password = SP.getString("password",
                "");

        Log.d(DEBUG_FLAG, "username : " + username);
        Log.d(DEBUG_FLAG, "password : " + password);

        if ("".equals(username) || "".equals(password)) {
            return false;
        } else {
            return true;
        }

    }

    private void logout(){
        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor SPE = SP.edit();

        SPE.putString("username", "");
        SPE.putString("password", "");
        SPE.apply();
    }

    private void clearBackStackFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        // clear the back stack for fragmentManager
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }

        titleStack.clear();
    }

    /**
     * Check the service is alive or not
     *
     * @param serviceClass
     * @return Alive state of the service
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setTitle(CharSequence title) {
        titleStack.push(mTitle.toString());
        mTitle = title;
        if(title == null) {
            Log.d(DEBUG_FLAG, "title null");
            return;
        }
        if(getSupportActionBar() == null) {
            Log.d(DEBUG_FLAG, "getSupportActionBar null");
            return;
        }
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        int countStackSize = getFragmentManager().getBackStackEntryCount(), titleStackSize = titleStack.size();

        Log.d(DEBUG_FLAG, countStackSize + " / " + titleStackSize);

        if(countStackSize == 0 && countStackSize == 0){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_close_confirm)
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .setPositiveButton(getString(R.string.comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            onDestroy();
                        }
                    }).create().show();
        }else if(countStackSize > titleStackSize){
            getFragmentManager().popBackStack();
        }else if(countStackSize == titleStackSize){
            mTitle = titleStack.pop();
            getSupportActionBar().setTitle(mTitle);// the reason why the code is written like this is to avoid to push title into titleStack
            getFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        NewsRecieveService.startActionUNREGISTER(getApplicationContext());
        super.onDestroy();
    }

    @Override
    public void onChangeTitle(String title) {
        setTitle(title);
    }

    @Override
    public void onReceiverRegister(BroadcastReceiver receiver, IntentFilter filter) {
        registerReceiver(receiver, filter);
    }

    @Override
    public void onReceiverUnregister(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        String navigationTilte = mDrawerList.getAdapter().getItem(position)
                .toString();
        Fragment fragment = null;

        if (getResources().getString(R.string.home_page)
                .equals(navigationTilte)) {
            fragment = mHomePageFragment;
            setTitle(navigationTilte);
        } else if (getResources().getString(R.string.login).equals(
                navigationTilte)) {
            (new LoginDialog(this)).show(getFragmentManager(), "Dialog");
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        } else if (getResources().getString(R.string.logout).equals(
                navigationTilte)) {
            changeNavigationNormalList();
            logout();
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        } else {
            Log.e(DEBUG_FLAG, "Select no definition fragment!");
            mDrawerList.setItemChecked(position, true);
            setTitle(mNavigationNormalList[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        if (fragment != null) {
            clearBackStackFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void changeNavigationNormalList() {
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationNormalList));
        selectItem(0);
    }

    public void changeNavigationLoginList() {
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationLoginList));
        selectItem(0);
    }
}
