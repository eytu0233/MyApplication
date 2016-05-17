package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.ncku.application.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 此頁面是用來顯示最新消息或最新訊息的內容
 */
public class NewsViewerFragment extends Fragment {

    private static final String DEBUG_FLAG = NewsViewerFragment.class.getName();
    private RelativeLayout msgViewer;
    private WebView msgContents;
    private TextView msgTitle, msgUnit, msgDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsViewerFragment newInstance() {
        return new NewsViewerFragment();
    }

    public NewsViewerFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_news_viewer, container,
                false);

        msgViewer = (RelativeLayout) rootView.findViewById(R.id.msgViewer);
        msgViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 覆蓋掉推播訊息的長按事件，避免進入刪除模式
            }
        });

        msgTitle = (TextView) rootView.findViewById(R.id.txtMsgTitle);
        msgTitle.setText(getArguments().getString("title"));

        msgUnit = (TextView) rootView.findViewById(R.id.txtMsgUnit);
        msgUnit.setText(getArguments().getString("unit"));

        msgDate = (TextView) rootView.findViewById(R.id.txtMsgDate);
        msgDate.setText(getArguments().getString("date"));

        msgContents = (WebView) rootView.findViewById(R.id.webContesViewer);
        Log.d(DEBUG_FLAG, getArguments().getString("contents"));
        msgContents.loadDataWithBaseURL("file:///android_asset/",
                getArguments().getString("contents"), "text/html",
                "utf-8", null);

        return rootView;
    }

}
