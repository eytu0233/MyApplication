package edu.ncku.application.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import edu.ncku.application.model.ContactInfo;
import edu.ncku.application.io.file.ContactInfoReaderTask;
import edu.ncku.application.R;


public class LibContactFragment extends Fragment {

    private static final String DEBUG_FLAG = LibContactFragment.class.getName();

    public final static String CSS_STYLE ="<link rel=\"stylesheet\" type=\"text/css\" href=\"http://m.lib.ncku.edu.tw/css/mobile.css\" /><style>* {font-size:20px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;}pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;}</style>";
    private static final String CONTACT_PHONE = "聯絡電話";
    private static final String CONTACT_IMG = "<img src=\"contact.png\">";
    private static final String CONTACT_PHONE_SUPER_LINK = "<a href=\"#\" onClick=\"window.PhoneCall.telext('%s');\">%s</a><br />";
    private static final String DEPT_IMG = "<img src=\"dept.png\">";
    private static final String CONTACT_EMAIL_SUPER_LINK = "<a href=\"mailto:%s\">%s</a><br /><br />";

    private PhoneCall phoneCall = new PhoneCall();

    private String html = "";

    public class PhoneCall{

        //After API 17, you will have to annotate each method with @JavascriptInterface within your class that you'd like to access from Javascript.

        @JavascriptInterface
        public void telext(String telStr) {
            Log.d(DEBUG_FLAG, telStr);
            Uri uri = Uri.parse("tel:"+telStr);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibContactFragment newInstance() {
        LibContactFragment fragment = new LibContactFragment();
        return fragment;
    }

    public LibContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ContactInfoReaderTask contactInfoReaderTask = new ContactInfoReaderTask(getActivity().getApplicationContext());
            contactInfoReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ArrayList<ContactInfo> contactInfos = contactInfoReaderTask.get(3, TimeUnit.SECONDS);
            if(contactInfos == null){
                html = null;
            }else{
                String centralPhone = "";
                for(ContactInfo contactInfo : contactInfos){
                    if(contactInfo.getDivision().equals(CONTACT_PHONE)){
                        html += CONTACT_IMG;
                        html += CONTACT_PHONE;
                        centralPhone = convert2Telext(contactInfo.getPhone().split("#")[0]);
                        html += String.format(CONTACT_PHONE_SUPER_LINK, convert2Telext(contactInfo.getPhone()), contactInfo.getPhone());
                    }else{
                        html += DEPT_IMG;
                        html += contactInfo.getDivision() + "<span class=\"hourscolor\">";
                        html += String.format(CONTACT_PHONE_SUPER_LINK, centralPhone + convert2Telext(contactInfo.getPhone()), contactInfo.getPhone());
                        if(contactInfo.getEmail()!=null && !contactInfo.getEmail().isEmpty()){
                            html += String.format(CONTACT_EMAIL_SUPER_LINK, contactInfo.getEmail(), contactInfo.getEmail());
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lib_contact, container,
                false);
        WebView webView = (WebView) rootView.findViewById(R.id.lib_contact_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(phoneCall, "PhoneCall");
        webView.loadDataWithBaseURL("file:///android_asset/", CSS_STYLE + ((html != null) ? html : this.getString(R.string.lib_contact_info)), "text/html",
                "utf-8", null);
        return rootView;
    }

    private String convert2Telext(String s){
        return Pattern.compile("[^0-9#]").matcher(s).replaceAll("").replace('#', ',');
    }

}
