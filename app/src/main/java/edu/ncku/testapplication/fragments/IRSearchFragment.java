package edu.ncku.testapplication.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.ncku.testapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IRSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IRSearchFragment extends Fragment {

    private static final String DEBUG_FLAG = IRSearchFragment.class.getName();

    public static final String KEYWORD = "keyword";

    private static final String SEARCH_URL = "http://m.lib.ncku.edu.tw/catalogs/KeywordSearch.php";
    private static final String BIB_URL = "http://m.lib.ncku.edu.tw/catalogs/KeywordbibSearch.php?lan=cht&Keyword=";

    private String url;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IRSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IRSearchFragment newInstance(String keyword) {
        IRSearchFragment fragment = new IRSearchFragment();
        Bundle args = new Bundle();
        args.putString(KEYWORD, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    public static IRSearchFragment newInstance() {
        return new IRSearchFragment();
    }

    public IRSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = BIB_URL + getArguments().getString(KEYWORD);
        } else {
            url = SEARCH_URL;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web, container,
                false);

        WebView webView = (WebView) rootView.findViewById(R.id.irWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

        });
        webView.loadUrl(url);

        return rootView;
    }
}
