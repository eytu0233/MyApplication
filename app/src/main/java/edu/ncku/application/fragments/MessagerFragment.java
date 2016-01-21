package edu.ncku.application.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.ncku.application.io.MsgsReaderTask;
import edu.ncku.application.LoadMoreListView;
import edu.ncku.testapplication.R;
import edu.ncku.application.util.ListMsgsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagerFragment extends Fragment implements /*SwipeRefreshLayout.OnRefreshListener,*/
        LoadMoreListView.OnLoadMore{

    private static final String DEBUG_FLAG = MessagerFragment.class.getName();
    private static final String USER_NAME = "USERNAME";

    private static int PRELOAD_MSGS_NUM;

    private int numShowedMsgs = 0;

    private ProgressBar progressBar;
    private TextView textView;
    private LoadMoreListView listView;
    //private SwipeRefreshLayout swipe;
    private Handler mHandler = new Handler();
    private ListMsgsAdapter listViewAdapter;
    private SharedPreferences sp;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagerFragment newInstance(String userName) {
        MessagerFragment fragment = new MessagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_NAME, userName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MessagerFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        PRELOAD_MSGS_NUM = Integer.valueOf(sp.getString("PRELOAD_MSGS_MAX",
                "10"));

        if (PRELOAD_MSGS_NUM <= 0) {
            Log.e(DEBUG_FLAG, "PRELOAD_MSGS_NUM is smaller than zero");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_messager, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.msgProgressBar);
        textView = (TextView) rootView.findViewById(R.id.msgTip);
        listView = (LoadMoreListView) rootView.findViewById(R.id.msgListView);
        listView.setLoadMoreListen(this);
        /*swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.msgSwipe);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);*/
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(DEBUG_FLAG, "ReaderTask start!");
                    if (updateList()) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    @Override
    public void loadMore() {
        try {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    listViewAdapter.showMoreOldMessaage(Integer.valueOf(sp
                            .getString("LOAD_MSGS_MAX", "10")));
                    numShowedMsgs = listViewAdapter.getCount();
                    Log.v("MessageListActivity", "show : " + numShowedMsgs);

                    listView.onLoadComplete();

                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean updateList() throws Exception {
        if(numShowedMsgs < PRELOAD_MSGS_NUM){
            numShowedMsgs = PRELOAD_MSGS_NUM;
        }
        MsgsReaderTask newsReaderTask = new MsgsReaderTask(this, this.getArguments().getString(USER_NAME), numShowedMsgs);
        newsReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        listViewAdapter = newsReaderTask.get();

        if (listViewAdapter != null) {
            numShowedMsgs = listViewAdapter.getCount();
            Log.v(DEBUG_FLAG, "UpdateList finish : " + numShowedMsgs);
            return true;
        } else {
            return false;
        }
    }

}
