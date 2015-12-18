package edu.ncku.testapplication.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ncku.testapplication.R;

public class PrefFragment extends PreferenceFragment {

    private static final String DEBUG_TAG = PrefFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.white));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.preferences);
        final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("MESSAGER_SUBSCRIPTION");

        if (checkboxPref != null) {
            checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(final Preference preference, Object newValue) {

                    final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    final SharedPreferences.Editor SPE = SP.edit();

                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_subscription)
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    boolean sub = SP.getBoolean(preference.getKey(), true);
                                    SPE.putBoolean(preference.getKey(), !sub);
                                    SPE.apply();
                                    checkboxPref.setChecked(!sub);
                                }
                            })
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create().show();
                    return true;
                }
            });
        }

		    /* Below code is to avoid the default value not to be set */
        ListPreference preload_list_preference = (ListPreference) getPreferenceManager().findPreference("PRELOAD_MSGS_MAX");
        String preload_list_preference_value = preload_list_preference.getValue();
        if (preload_list_preference_value == null || preload_list_preference_value.equals("0")) {
            preload_list_preference.setValueIndex(0);
        }

        ListPreference load_list_preference = (ListPreference) getPreferenceManager().findPreference("LOAD_MSGS_MAX");
        String load_list_preference_value = load_list_preference.getValue();
        if (load_list_preference_value == null || load_list_preference_value.equals("0")) {
            load_list_preference.setValueIndex(0);
        }

        ListPreference out_of_date_preference = (ListPreference) getPreferenceManager().findPreference("OUT_OF_DATE");
        String out_of_date_preference_value = out_of_date_preference.getValue();
        if (out_of_date_preference_value == null || out_of_date_preference_value.equals("0")) {
            load_list_preference.setValueIndex(0);
        }

    }


}
