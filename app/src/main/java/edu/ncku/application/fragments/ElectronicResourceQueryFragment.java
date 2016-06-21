package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.ncku.application.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ElectronicResourceQueryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElectronicResourceQueryFragment extends Fragment {

    private static final String RESOURCE_URL = "http://m.lib.ncku.edu.tw/index.php#eResource";

    public ElectronicResourceQueryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElectronicResourceQueryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElectronicResourceQueryFragment newInstance() {
        ElectronicResourceQueryFragment fragment = new ElectronicResourceQueryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_electronic_resource_query, container, false);

        WebView webView = (WebView) rootView.findViewById(R.id.electronicResourceWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

        });
        webView.loadUrl(RESOURCE_URL);

        return rootView;
    }
}
