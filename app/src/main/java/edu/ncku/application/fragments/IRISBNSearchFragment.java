package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.ncku.application.R;
import edu.ncku.application.util.EnvChecker;

/**
 * 使用ISBN參數來向IR搜尋網頁取得相關資訊
 */
public class IRISBNSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String DEBUG_FLAG = IRISBNSearchFragment.class.getName();

    private static final String ISBN = "ISBN";

    private static final String ISBN_SEARCH_URL = "http://m.lib.ncku.edu.tw/catalogs/ISBNBibSearch.php?lan=" + ((EnvChecker.isLunarSetting())?"cht":"eng") + "&ISBN=";

    // TODO: Rename and change types of parameters
    private String isbn;

    public static IRISBNSearchFragment newInstance(String isbn) {
        IRISBNSearchFragment fragment = new IRISBNSearchFragment();
        Bundle args = new Bundle();
        args.putString(ISBN, isbn);
        fragment.setArguments(args);
        return fragment;
    }

    public IRISBNSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // 使fragment驅動onCreateOptionsMenu
        if (getArguments() != null) {
            isbn = getArguments().getString(ISBN);
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
        webView.loadUrl(ISBN_SEARCH_URL + isbn);

        return rootView;
    }

}
