package edu.ncku.application.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.ncku.testapplication.R;
import edu.ncku.application.io.FloorInfoReaderTask;

public class LibFloorFragment extends Fragment {

    private static final String DEBUG_FLAG = LibFloorFragment.class.getName();

    private String html = "";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibFloorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibFloorFragment newInstance() {
        LibFloorFragment fragment = new LibFloorFragment();
        return fragment;
    }

    public LibFloorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FloorInfoReaderTask floorInfoReaderTask = new FloorInfoReaderTask(getActivity().getApplicationContext());
            floorInfoReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Map<String, String> floorInfo = floorInfoReaderTask.get(3, TimeUnit.SECONDS);
            if(floorInfo == null){
                html = null;
                Log.d(DEBUG_FLAG, "floorInfoReaderTask return null");
            }else{
                for(String key : floorInfo.keySet()){
                    html += "<span class=\"floortitle\">" + key + "</span><br></br>";
                    html += floorInfo.get(key) + "<br><br>";
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lib_floor, container,
                false);

        WebView webView = (WebView) rootView.findViewById(R.id.lib_floor_webView);
        webView.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://m.lib.ncku.edu.tw/css/mobile.css\" />" + ((html!=null)?html:this.getString(R.string.lib_floor_info)) , "text/html",
                "utf-8", null);
        return rootView;
    }

}
