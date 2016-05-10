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
 * Use the {@link PersonalBorrowFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 顯示個人借閱網頁頁面
 */
public class PersonalBorrowFragment extends Fragment {
    private static final String DEBUG_FLAG = IRSearchFragment.class.getName();

    private static final String URL = "http://m.lib.ncku.edu.tw/patroninfo/login_my_account.php";

    private WebView webView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IRSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalBorrowFragment newInstance() {
        return new PersonalBorrowFragment();
    }

    public PersonalBorrowFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_web, container,
                false);

        webView = (WebView) rootView.findViewById(R.id.irWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

        });
        webView.loadUrl(URL);

        return rootView;
    }

}
