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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import edu.ncku.testapplication.R;
import edu.ncku.testapplication.util.ListViewInfoAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibInfoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibInfoListFragment extends Fragment {

    private String DEBUG_FLAG = LibInfoListFragment.class.getName();

    private ListView listview;

    private NetworkInfo currentNetworkInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibInfoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibInfoListFragment newInstance() {
        return new LibInfoListFragment();
    }

    public LibInfoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = ((ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lib_info_list, container,
                false);
        listview = (ListView) rootView.findViewById(R.id.infoListView);

        String[] lib_info_list = getResources().getStringArray(
                R.array.lib_info_list);

        listview.setAdapter(new ListViewInfoAdapter(this
                .getActivity().getApplicationContext(), lib_info_list));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                listview.setEnabled(false);
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = LibInfoOpenTimeFragment.newInstance();
                        break;
                    case 1:
                        fragment = LibFloorFragment.newInstance();
                        break;
                    case 2:
                        fragment = LibContactFragment.newInstance();
                        break;
                    case 3:
                        if (checkNetwork(getString(R.string.map_network))) {
                            fragment = LibMapFragment.newInstance();
                        }
                        break;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity()
                            .getFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null)
                            .add(R.id.content_frame, fragment).commit();
                }
                listview.setEnabled(true);
            }

        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean checkNetwork() {
        if (currentNetworkInfo == null || !currentNetworkInfo.isConnected()) {
            return false;
        }

        return true;
    }

    private boolean checkNetwork(String toast) {
        if (currentNetworkInfo == null || !currentNetworkInfo.isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
