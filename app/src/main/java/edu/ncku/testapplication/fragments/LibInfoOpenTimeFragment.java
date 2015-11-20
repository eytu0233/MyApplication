package edu.ncku.testapplication.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ncku.testapplication.R;
import edu.ncku.testapplication.util.OpenTimeExpListAdapter;

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
    HashMap<String, List<String>> mListDataChild; // 內容

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lib_info_open_time,
                container, false);

        mOpenTimeExpListView = (ExpandableListView) rootView
                .findViewById(R.id.openTimeExpListView);

        // 列表資料
        prepareListData();

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
        mOpenTimeExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                TextView title = (TextView) v.findViewById(R.id.txtTitle);
                title.setTextColor(Color.rgb(255, 116, 21));
            }

        });
        mOpenTimeExpListView
                .setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        // TODO Auto-generated method stub

                    }

                });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void prepareListData() {
        // TODO Auto-generated method stub
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<String>>();

        for (String openTimeHeader : getResources().getStringArray(
                R.array.lib_open_time_list)) {
            mListDataHeader.add(openTimeHeader);
        }

        ArrayList<String> main_lib = new ArrayList<String>();
        main_lib.add(getString(R.string.open_time_main_lib));
        ArrayList<String> self_studying = new ArrayList<String>();
        self_studying
                .add(getString(R.string.open_time_self_studying_reading_room));
        ArrayList<String> medical_branch = new ArrayList<String>();
        medical_branch.add(getString(R.string.open_time_medical_branch_lib));
        ArrayList<String> departments = new ArrayList<String>();
        departments.add(getString(R.string.open_time_departments));

        mListDataChild.put(mListDataHeader.get(0), main_lib);
        mListDataChild.put(mListDataHeader.get(1), self_studying);
        mListDataChild.put(mListDataHeader.get(2), medical_branch);
        mListDataChild.put(mListDataHeader.get(3), departments);

    }

}
