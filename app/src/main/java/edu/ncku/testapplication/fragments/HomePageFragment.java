package edu.ncku.testapplication.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import edu.ncku.testapplication.R;
import edu.ncku.testapplication.util.ITitleChangeListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    private static final String DEBUG_FLAG = HomePageFragment.class.getName();

    private Fragment mLibInfoListFragment;
    private Fragment mIRSearchFragment;
    private Fragment mRecentActivityFragment;
    private Fragment mNewsFragment;

    private ImageView mLibInfoImageView;
    private ImageView mNewsImageView;
    private ImageView mIRSearchImageView;
    private ImageView mPersonalBorrowImageView;
    private ImageView mActivityImageView;
    private ImageView mScannerImageView;

    private Context context;
    private ITitleChangeListener titleChangeListener;
    private NetworkInfo currentNetworkInfo;

    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    public HomePageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsFragment = NewsFragment.newInstance();
        mIRSearchFragment = IRSearchFragment.newInstance();
        mLibInfoListFragment = LibInfoListFragment.newInstance();
        mRecentActivityFragment = RecentActivityFragment.newInstance();

        context = this.getActivity().getApplicationContext();
        ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_page,
                container, false);
        mLibInfoImageView = (ImageView) rootView.findViewById(R.id.libInfoImgBtn);
        mLibInfoImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mLibInfoListFragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, mLibInfoListFragment).commit();
                    titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_info));
                }
            }

        });
        mIRSearchImageView = (ImageView) rootView.findViewById(R.id.IRSearchImgBtn);
        mIRSearchImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mIRSearchFragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, mIRSearchFragment).commit();
                    titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_search));
                }
            }

        });
        mActivityImageView = (ImageView) rootView.findViewById(R.id.activityImgBtn);
        mActivityImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mRecentActivityFragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, mRecentActivityFragment).commit();
                    titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_activity));
                }
            }

        });
        mNewsImageView = (ImageView) rootView.findViewById(R.id.newsImgBtn);
        mNewsImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mNewsFragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, mNewsFragment).commit();
                    titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_news));
                }
            }

        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            titleChangeListener = (ITitleChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ITitleChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        titleChangeListener = null;
    }

    private boolean checkNetworkToast() {
        if (currentNetworkInfo == null || !currentNetworkInfo.isConnected()) {
            Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
