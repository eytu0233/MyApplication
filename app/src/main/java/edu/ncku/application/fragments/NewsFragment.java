package edu.ncku.application.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncku.application.LoadMoreListView;
import edu.ncku.application.io.NewsReaderTask;
import edu.ncku.application.service.DataReceiveService;
import edu.ncku.application.util.IReceiverRegisterListener;
import edu.ncku.application.util.ListNewsAdapter;
import edu.ncku.testapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoadMoreListView.OnLoadMore {

    private static final String DEBUG_FLAG = NewsFragment.class.getName();

    public static final String FINISH_FLUSH_FLAG = "FinishFlushFlag";

    private static int PRELOAD_MSGS_NUM;

    private Handler mHandler = new Handler();

    private ProgressBar progressBar;
    private TextView newsTip;
    private LoadMoreListView listView;
    private SwipeRefreshLayout swipe;

    private NewsReceiver receiver;
    private ListNewsAdapter listViewAdapter;
    private SharedPreferences sp;
    private IReceiverRegisterListener receiverRegisterListener;

    private int numShowedMsgs = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        PRELOAD_MSGS_NUM = Integer.valueOf(sp.getString("PRELOAD_MSGS_MAX",
                "10"));

        if (PRELOAD_MSGS_NUM <= 0) {
            Log.e(DEBUG_FLAG, "PRELOAD_MSGS_NUM is smaller than zero");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_news,
                container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.newsProgressBar);
        newsTip = (TextView) rootView.findViewById(R.id.newsTip);
        listView = (LoadMoreListView) rootView.findViewById(R.id.listView);
        listView.setLoadMoreListen(this);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swip_index);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        /* register NewsReceiver */
        receiver = new NewsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_RECEIVER");
        receiverRegisterListener.onReceiverRegister(receiver, filter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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
                        newsTip.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 500);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        receiverRegisterListener.onReceiverUnregister(receiver);
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            receiverRegisterListener = (IReceiverRegisterListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ITitleChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        receiverRegisterListener = null;
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

    @Override
    public void onRefresh() {
        try {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onceActiveUpdateMessageData();
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListAdapter(final ListAdapter adapter) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                listView.setAdapter(adapter);
            }
        });
    }

    /**
     * broadcast to update message data once
     */
    private void onceActiveUpdateMessageData() {
        DataReceiveService.startActionONCE(getActivity().getApplicationContext());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(false);
            }
        }, 2000);
    }

    private boolean updateList() throws Exception {
		/* Read data in background and reflesh the listview of this activity */
        if(numShowedMsgs < PRELOAD_MSGS_NUM){
            numShowedMsgs = PRELOAD_MSGS_NUM;
        }
        Log.v(DEBUG_FLAG, "want to show : "
                + numShowedMsgs);

        NewsReaderTask newsReaderTask = new NewsReaderTask(this, numShowedMsgs);
        newsReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        listViewAdapter = newsReaderTask.get();
        setListAdapter(listViewAdapter);

        if (listViewAdapter != null) {
            numShowedMsgs = listViewAdapter.getCount();
            newsTip.setVisibility(View.INVISIBLE);
            Log.v(DEBUG_FLAG, "UpdateList finish : " + numShowedMsgs);
            return true;
        } else {
            return false;
        }
    }

    /**
     * broadcast receiver
     *
     * @author root
     */
    private class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                int numMsgs = bundle.getInt("numNews");
                if (numMsgs > 0) {
                    Log.v(DEBUG_FLAG, "Get new messages : " + numMsgs);
                    numShowedMsgs += numMsgs;
                    updateList();
                }

                String flag = bundle.getString("flag");
                if (null != flag) {
                    if (!FINISH_FLUSH_FLAG.equals(flag)) {
                        Toast.makeText(context, flag, Toast.LENGTH_SHORT)
                                .show();
                    }
                    swipe.setRefreshing(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
