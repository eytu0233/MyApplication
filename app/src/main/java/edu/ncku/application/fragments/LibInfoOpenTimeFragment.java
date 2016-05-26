package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.ncku.application.R;
import edu.ncku.application.io.file.LibOpenTimeReaderTask;
import edu.ncku.application.adapter.OpenTimeExpListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibInfoOpenTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 圖書館開放時間頁面，使用ExpandableListView顯示
 */
public class LibInfoOpenTimeFragment extends Fragment {

    private String DEBUG_FLAG = LibInfoOpenTimeFragment.class.getName();

    ExpandableListView mOpenTimeExpListView;
    OpenTimeExpListAdapter mOpenTimeExpListAdapter;
    List<String> mListDataHeader; // 標題
    Map<String, List<String>> mListDataChild; // 內容

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibInfoOpenTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibInfoOpenTimeFragment newInstance() {
        return new LibInfoOpenTimeFragment();
    }

    public LibInfoOpenTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 準備列表資料
        prepareListData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lib_info_open_time,
                container, false);

        mOpenTimeExpListView = (ExpandableListView) rootView
                .findViewById(R.id.openTimeExpListView);

		/* listIv-圖示, listDataHeader-標題, listDataChild-內容 */
        mOpenTimeExpListAdapter = new OpenTimeExpListAdapter(this.getActivity()
                .getApplicationContext(), mListDataHeader, mListDataChild);

		/* 取得螢幕寬度 */
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mOpenTimeExpListView.setIndicatorBounds(width - 100, width);
        } else {
            mOpenTimeExpListView.setIndicatorBoundsRelative(width - 100, width);
        }

        // 將列表資料加入至展開列表單
        mOpenTimeExpListView.setAdapter(mOpenTimeExpListAdapter);
        mOpenTimeExpListView
                .setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(final int groupPosition) {
                        (new Handler()).post(new Runnable() {
                            @Override
                            public void run() {
                                TextView title = mOpenTimeExpListAdapter.getGroupHeaderView(groupPosition);
                                title.setTextColor(android.graphics.Color.rgb(0, 0, 0));
                            }
                        });

                    }
                });
        mOpenTimeExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, final View v, int groupPosition, long id) {
                (new Handler()).post(new Runnable() {
                    @Override
                    public void run() {
                        TextView title = (TextView) v.findViewById(R.id.txtTitle);
                        title.setTextColor(android.graphics.Color.rgb(247, 80, 0));
                    }
                });
                return false;
            }
        });

        return rootView;
    }

    /**
     * 準備列表資料，預設從文字資源檔取得，假如有檔案(來自網路)則優先讀取
     */
    private void prepareListData() {
        // TODO Auto-generated method stub
        mListDataHeader = new ArrayList<String>();
        ArrayList<String> main_lib = new ArrayList<String>();
        ArrayList<String> self_studying = new ArrayList<String>();
        ArrayList<String> learn_comm = new ArrayList<String>();
        ArrayList<String> medical_branch = new ArrayList<String>();
        ArrayList<String> departments = new ArrayList<String>();

        try {
            LibOpenTimeReaderTask openTimeReaderTask = new LibOpenTimeReaderTask(getActivity().getApplicationContext());
            openTimeReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mListDataChild = openTimeReaderTask.get(3, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String openTimeHeader : getResources().getStringArray(
                R.array.lib_open_time_unit_list)) {
            mListDataHeader.add(openTimeHeader);
        }

        /* 當應用程式尚未連上網頁時，將以預設值(文字資源)顯示 */
        if (mListDataChild == null || mListDataChild.keySet().size() != mListDataHeader.size()) {
            Log.d(DEBUG_FLAG, "預設值");
            main_lib.add(getString(R.string.open_time_main_lib));
            self_studying
                    .add(getString(R.string.open_time_self_studying_reading_room));
            learn_comm.add(getString(R.string.open_time_learn_comm));
            medical_branch.add(getString(R.string.open_time_medical_branch_lib));

            departments.add(getString(R.string.open_time_departments));
            departments.add(getString(R.string.open_time_departments_vacation));

            mListDataChild = new HashMap<String, List<String>>();
            mListDataChild.put(mListDataHeader.get(0), main_lib);
            mListDataChild.put(mListDataHeader.get(1), self_studying);
            mListDataChild.put(mListDataHeader.get(2), learn_comm);
            mListDataChild.put(mListDataHeader.get(3), medical_branch);
            mListDataChild.put(mListDataHeader.get(4), departments);
        }

    }

}
