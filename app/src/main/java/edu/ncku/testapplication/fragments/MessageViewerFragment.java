package edu.ncku.testapplication.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import edu.ncku.testapplication.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageViewerFragment extends Fragment {

    private WebView msgContents;
    private TextView msgTitle, msgUnit, msgDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageViewerFragment newInstance() {
        return new MessageViewerFragment();
    }

    public MessageViewerFragment() {
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

        msgTitle = (TextView) rootView.findViewById(R.id.txtMsgTitle);
        msgTitle.setText(getArguments().getString("title"));

        msgUnit = (TextView) rootView.findViewById(R.id.txtMsgUnit);
        msgUnit.setText(getArguments().getString("unit"));

        msgDate = (TextView) rootView.findViewById(R.id.txtMsgDate);
        msgDate.setText(getArguments().getString("date"));

        msgContents = (WebView) rootView.findViewById(R.id.webContesViewer);
        msgContents.loadDataWithBaseURL("file:///android_asset/",
                getArguments().getString("contents"), "text/html",
                "utf-8", null);

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

}
