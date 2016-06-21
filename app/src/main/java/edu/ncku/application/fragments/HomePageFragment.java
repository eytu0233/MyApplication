package edu.ncku.application.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.ncku.application.R;
import edu.ncku.application.io.network.VisitorRecieveTask;
import edu.ncku.application.util.EnvChecker;
import edu.ncku.application.util.ITitleChangeListener;
import edu.ncku.application.util.Preference;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 主頁面，提供使用者選擇六個主要功能(最新消息、館藏查詢、個人借閱、本館資訊、最近活動、書籍掃描)
 */
public class HomePageFragment extends Fragment {

    private static final String DEBUG_FLAG = HomePageFragment.class.getName();

    private Fragment mLibInfoListFragment;
    private Fragment mIRSearchFragment;
    private Fragment mRecentActivityFragment;
    private Fragment mNewsFragment;
    private Fragment mPersonalBorrowFragment;

    private ImageView mLibInfoImageView;
    private ImageView mNewsImageView;
    private ImageView mIRSearchImageView;
    private ImageView mPersonalBorrowImageView;
    private ImageView mActivityImageView;
    private ImageView mScannerImageView;
    private EditText searchBarEditText;
    private LinearLayout footer;
    private TextView visitorText;

    private Context context;
    private Activity activity;
    private ITitleChangeListener titleChangeListener; //標題變更的監聽介面(實體由MainActivity
    private Toast toast;

    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    public HomePageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mNewsFragment = NewsFragment.getInstance(-1);
        mIRSearchFragment = IRSearchFragment.newInstance();
        mLibInfoListFragment = LibInfoListFragment.newInstance();
//        mLibInfoListFragment = InfoListFragment.newInstance();
        mRecentActivityFragment = RecentActivityFragment.newInstance();
        mPersonalBorrowFragment = PersonalBorrowFragment.newInstance();

        context = this.getActivity().getApplicationContext();
        activity = this.getActivity();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.VISITORS_RECEIVER");
        activity.registerReceiver(mVisitorReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_page,
                container, false);
        mLibInfoImageView = (ImageView) rootView.findViewById(R.id.libInfoImgBtn);
        mLibInfoImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                addFragment(mLibInfoListFragment, getResources().getString(R.string.homepage_ic_info));
            }

        });
        mIRSearchImageView = (ImageView) rootView.findViewById(R.id.IRSearchImgBtn);
        mIRSearchImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!checkNetworkToast()) return;

                addFragment(mIRSearchFragment, getResources().getString(R.string.homepage_ic_search));
            }

        });
        mActivityImageView = (ImageView) rootView.findViewById(R.id.activityImgBtn);
        mActivityImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                addFragment(mRecentActivityFragment, getResources().getString(R.string.homepage_ic_activity));
            }

        });
        mNewsImageView = (ImageView) rootView.findViewById(R.id.newsImgBtn);
        mNewsImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                addFragment(mNewsFragment, getResources().getString(R.string.homepage_ic_news));
            }

        });
        mPersonalBorrowImageView = (ImageView) rootView.findViewById(R.id.borrowImgBtn);
        mPersonalBorrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkNetworkToast()) return;

                addFragment(mPersonalBorrowFragment, getResources().getString(R.string.homepage_ic_barrow));
            }
        });
        mScannerImageView = (ImageView) rootView.findViewById(R.id.isbnImgBtn);
        mScannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkNetworkToast()) return;

                final ProgressFragment progressFragment = ProgressFragment.newInstance();

                final FragmentManager fragmentManager = getActivity()
                        .getFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .add(R.id.content_frame, progressFragment).commit();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.popBackStack();
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.setPrompt(getString(R.string.scan_isbn));
                        integrator.initiateScan();
                    }
                }, 500);
            }
        });
        searchBarEditText = (EditText) rootView.findViewById(R.id.searchBarEditText);
        searchBarEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!checkNetworkToast()) return true;

                switch (actionId) {
                    case EditorInfo.IME_NULL:
                    case EditorInfo.IME_ACTION_SEND:
                    case EditorInfo.IME_ACTION_DONE:
                        addFragment(IRSearchFragment.newInstance(v.getText().toString()), getResources().getString(R.string.homepage_ic_search));
                        v.setText("");
                        View view = activity.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        break;
                }
                return true;
            }
        });
        visitorText = (TextView) rootView.findViewById(R.id.visitorsText);
        visitorText.setText(Preference.getVisitor(context));

        footer = (LinearLayout) rootView.findViewById(R.id.footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Handler()).post(new Runnable() {
                    @Override
                    public void run() {
                        visitorText.setText(R.string.wait_update);
                        refreshVisitor(false, true);
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        refreshVisitor(true, true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        /* 只有在Home頁面且登入時時，才顯示設定按鈕 */
        MenuItem settingItem = menu.findItem(R.id.settingMenuItem);

        /* 只有在Home頁面且登入時時，才顯示設定按鈕 */
        if (settingItem != null) {
            settingItem.setVisible(Preference.isLoggin(context));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        Preference.setVisitor(context, "");
        activity.unregisterReceiver(mVisitorReceiver);
        super.onDestroy();
    }

    /**
     * 註冊ITitleChangeListener介面的標題變更方法
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            titleChangeListener = (ITitleChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ITitleChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        titleChangeListener = null;
    }

    /**
     * 新增Fragment堆疊，驅動標題變更事件方法
     *
     * @param fragment
     * @param title 標題
     */
    private void addFragment(Fragment fragment, String title){
        if(fragment != null && !fragment.isAdded()) {
            FragmentManager fragmentManager = getActivity()
                    .getFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null)
                    .add(R.id.content_frame, fragment).commit();
            if(title != null && !title.isEmpty()) titleChangeListener.onChangeTitle(title);
        }
    }

    /**
        * 確認當前網路狀態
        *
        *@return conntected or not
        */
    private boolean checkNetworkToast() {
        if (!EnvChecker.pingGoogleDNS()) {
            if (toast == null)
            {
                toast = Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG);
            }
            toast.show();
            return false;
        }

        return true;
    }

    private void refreshVisitor(boolean isBackground, boolean isOnce){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(new VisitorRecieveTask(context, isBackground, isOnce), 1, TimeUnit.SECONDS);
        executor.shutdown();
    }

    private BroadcastReceiver mVisitorReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {

            Bundle bundle = intent.getExtras();
            String visitors = "";
            if(bundle != null){
                visitors = bundle.getString("visitors", "");
            }

            if(visitors != null && !visitors.isEmpty()){
                visitorText.setText(visitors);
            }else{
                visitorText.setText(R.string.update_fail);
                if (toast == null)
                {
                    toast = Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG);
                }
                toast.show();
            }
        }
    };
}
