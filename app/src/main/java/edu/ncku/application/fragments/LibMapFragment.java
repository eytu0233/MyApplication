package edu.ncku.application.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.ncku.application.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibMapFragment#getInstance} factory method to
 * create an instance of this fragment.
 * 顯示地利位置的頁面，預設以Google Map顯示，假如開啟失敗則以網頁形式顯示
 */
public class LibMapFragment extends Fragment {
    private static final String TAG = LibMapFragment.class.getName();
    private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

    private static View rootView;
    private static LibMapFragment instance;

    private GoogleMap mMap;

    /**
     *  為避免發生例外，此Fragment將只有單一實體
     * @return LibMapFragment實體
     */
    // TODO: Rename and change types and number of parameters
    public static LibMapFragment getInstance() {
        if(instance == null)
            instance = new LibMapFragment();
        return instance;
    }

    public LibMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        readyToGo();
        // Inflate the layout for this fragment
        if(rootView == null)
        rootView = inflater.inflate(R.layout.fragment_lib_map,
                container, false);
        initializeMap();
        return rootView;
    }

    protected boolean readyToGo() {
        GoogleApiAvailability checker=
                GoogleApiAvailability.getInstance();

        Context context = getActivity().getApplicationContext();
        int status = checker.isGooglePlayServicesAvailable(context);


        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManager(context) >= 2) {
                return(true); // 成功開啟Google Map
            }
            else {
                Toast.makeText(context, R.string.no_maps, Toast.LENGTH_LONG).show();
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
            }
        }
        else {
            /* 開啟失敗，打開地理位置網頁 */
            Toast.makeText(context, R.string.no_maps, Toast.LENGTH_LONG).show();
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
            Uri uri=Uri.parse("http://m.lib.ncku.edu.tw/map.html");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
        }

        return(false);
    }

    /**
     * 暫時沒有用到，請無視這個DialogFragment類別
     */
    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_ERROR_CODE="errorCode";

        static ErrorDialogFragment newInstance(int errorCode) {
            Bundle args=new Bundle();
            ErrorDialogFragment result=new ErrorDialogFragment();

            args.putInt(ARG_ERROR_CODE, errorCode);
            result.setArguments(args);

            return(result);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args=getArguments();
            GoogleApiAvailability checker=
                    GoogleApiAvailability.getInstance();

            return(checker.getErrorDialog(getActivity(),
                    args.getInt(ARG_ERROR_CODE), 0));
        }

        @Override
        public void onDismiss(DialogInterface dlg) {
            if (getActivity()!=null) {
                getActivity().finish();
            }
        }
    }

    /**
     * 初始化Google Map
     */
    private void initializeMap() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // check if map is created successfully or not
            if (mMap == null) {
                Log.e(TAG, "Sorry! unable to create maps");
                return;
            }

            setUpMap();
            Log.d(TAG, "Set up maps");
        }
    }

    /**
     * 移動地圖到參數指定的經緯度座標
     *
     * @param place 經緯度座標
     */
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * 設置地標
     */
    private void setUpMap() {
        // 建立位置的座標物件
        LatLng place = new LatLng(22.999770,120.219925);
        // 移動地圖
        moveMap(place);

        addMarker(place, "國立成功大學總圖書館", "台灣台南市東區大學路1號");
    }

    /**
     * 在地圖加入指定位置與標題的標記Icon
     *
      * @param place 經緯度座標
     * @param title 標題
     * @param snippet 副標題
     */
    private void addMarker(LatLng place, String title, String snippet) {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title)
                .snippet(snippet)
                .icon(icon);

        mMap.addMarker(markerOptions);
    }


    // following from
    // https://android.googlesource.com/platform/cts/+/master/tests/tests/graphics/src/android/opengl/cts/OpenGlEsVersionTest.java

  /*
   * Copyright (C) 2010 The Android Open Source Project
   *
   * Licensed under the Apache License, Version 2.0 (the
   * "License"); you may not use this file except in
   * compliance with the License. You may obtain a copy of
   * the License at
   *
   * http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in
   * writing, software distributed under the License is
   * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   * CONDITIONS OF ANY KIND, either express or implied. See
   * the License for the specific language governing
   * permissions and limitations under the License.
   */
    private static int getVersionFromPackageManager(Context context) {
        PackageManager packageManager=context.getPackageManager();
        FeatureInfo[] featureInfos=
                packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open
                // gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    }
                    else {
                        return 1; // Lack of property means OpenGL ES
                        // version 1
                    }
                }
            }
        }
        return 1;
    }

    /** @see FeatureInfo#getGlEsVersion() */
    private static int getMajorVersion(int glEsVersion) {
        return((glEsVersion & 0xffff0000) >> 16);
    }
}
