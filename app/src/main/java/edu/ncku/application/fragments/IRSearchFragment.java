package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.ncku.application.R;
import edu.ncku.application.util.EnvChecker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IRSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 開啟IR搜尋網頁，如果有關鍵字的參數則一併輸入，同時會檢查語言環境
 */
public class IRSearchFragment extends Fragment {

    private static final String DEBUG_FLAG = IRSearchFragment.class.getName();

    public static final String KEYWORD = "keyword";

    private static final String SEARCH_URL = "http://m.lib.ncku.edu.tw/catalogs/KeywordSearch%s.php";
    private static final String BIB_URL = "http://m.lib.ncku.edu.tw/catalogs/KeywordbibSearch.php?Keyword=%s";

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
        setHasOptionsMenu(true); // 使fragment驅動onCreateOptionsMenu
        if (getArguments() != null) {
            url = String.format(BIB_URL, getArguments().getString(KEYWORD));
        } else {
            url = String.format(SEARCH_URL, (EnvChecker.isLunarSetting())?"":"_eng");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.findItem(R.id.settingMenuItem).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
