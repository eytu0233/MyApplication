package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.ncku.application.io.LibOpenTimeReaderTask;
import edu.ncku.application.util.OpenTimeExpListAdapter;
import edu.ncku.testapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibInfoOpenTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
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
                    public void onGroupCollapse(int groupPosition) {
                        TextView title = mOpenTimeExpListAdapter.getGroupHeaderView(groupPosition);
                        title.setTextColor(android.graphics.Color.rgb(0, 0, 0));
                    }
                });
        mOpenTimeExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                TextView title = (TextView) v.findViewById(R.id.txtTitle);
                title.setTextColor(android.graphics.Color.rgb(247, 80, 0));
                return false;
            }
        });

        return rootView;
    }

    private void prepareListData() {
        // TODO Auto-generated method stub
        mListDataHeader = new ArrayList<String>();
        ArrayList<String> main_lib = new ArrayList<String>();
        ArrayList<String> self_studying = new ArrayList<String>();
        ArrayList<String> medical_branch = new ArrayList<String>();
        ArrayList<String> departments = new ArrayList<String>();

        try {
            LibOpenTimeReaderTask openTimeReaderTask = new LibOpenTimeReaderTask(getActivity().getApplicationContext());
            openTimeReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mListDataChild = openTimeReaderTask.get(3, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        for (String openTimeHeader : getResources().getStringArray(
                R.array.lib_open_time_unit_list)) {
            mListDataHeader.add(openTimeHeader);
        }

        if(mListDataChild == null || mListDataChild.keySet().size() != mListDataHeader.size()) {
            Log.d(DEBUG_FLAG, "預設值");
            main_lib.add(getString(R.string.open_time_main_lib));
            self_studying
                    .add(getString(R.string.open_time_self_studying_reading_room));
            medical_branch.add(getString(R.string.open_time_medical_branch_lib));
            departments.add(getString(R.string.open_time_departments));
            mListDataChild = new HashMap<String, List<String>>();
            mListDataChild.put(mListDataHeader.get(0), main_lib);
            mListDataChild.put(mListDataHeader.get(1), self_studying);
            mListDataChild.put(mListDataHeader.get(2), medical_branch);
            mListDataChild.put(mListDataHeader.get(3), departments);
        }


    }

}
