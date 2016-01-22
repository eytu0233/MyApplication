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
 * Use the {@link IRISBNSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IRISBNSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String DEBUG_FLAG = IRISBNSearchFragment.class.getName();

    private static final String ISBN = "ISBN";

    private static final String ISBN_SEARCH_URL = "http://m.lib.ncku.edu.tw/catalogs/ISBNBibSearch.php?lan=cht&ISBN=";

    // TODO: Rename and change types of parameters
    private String isbn;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isbn Parameter 1.
     * @return A new instance of fragment IRISBNSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
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
