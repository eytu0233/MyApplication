package edu.ncku.application.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncku.application.R;
import edu.ncku.application.util.ITitleChangeListener;


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
    private Fragment mPersonalBorrowFragment;

    private ImageView mLibInfoImageView;
    private ImageView mNewsImageView;
    private ImageView mIRSearchImageView;
    private ImageView mPersonalBorrowImageView;
    private ImageView mActivityImageView;
    private ImageView mScannerImageView;
    private EditText searchBarEditText;

    private Context context;
    private Activity activity;
    private ITitleChangeListener titleChangeListener;

    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    public HomePageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsFragment = NewsFragment.getInstance(-1);
        mIRSearchFragment = IRSearchFragment.newInstance();
        mLibInfoListFragment = LibInfoListFragment.newInstance();
        mRecentActivityFragment = RecentActivityFragment.newInstance();
        mPersonalBorrowFragment = PersonalBorrowFragment.newInstance();

        context = this.getActivity().getApplicationContext();
        activity = this.getActivity();
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
                if(!checkNetworkToast()) return;

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
        mPersonalBorrowImageView = (ImageView) rootView.findViewById(R.id.borrowImgBtn);
        mPersonalBorrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkNetworkToast()) return;

                if(mPersonalBorrowFragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, mPersonalBorrowFragment).commit();
                    titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_barrow));
                }
            }
        });
        searchBarEditText = (EditText) rootView.findViewById(R.id.searchBarEditText);
        searchBarEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!checkNetworkToast()) return true;

                switch(actionId){
                    case EditorInfo.IME_NULL:
                    case EditorInfo.IME_ACTION_SEND:
                    case EditorInfo.IME_ACTION_DONE:
                        FragmentManager fragmentManager = getActivity()
                                .getFragmentManager();
                        fragmentManager.beginTransaction().addToBackStack(null)
                                .add(R.id.content_frame, IRSearchFragment.newInstance(v.getText().toString())).commit();
                        titleChangeListener.onChangeTitle(getResources().getString(R.string.homepage_ic_search));
                        v.setText("");
                        View view = activity.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        break;
                }
                return true;
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

    /**
        * 確認當前網路狀態
        *
        *@return
        */
    private boolean checkNetworkToast() {
        NetworkInfo currentNetworkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (currentNetworkInfo == null || !currentNetworkInfo.isConnected()) {
            Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
