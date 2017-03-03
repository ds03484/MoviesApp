package com.example.android.moviesapp_version_2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.ArrayList;

/**
 * Created by ds034_000 on 2/2/2017.
 */

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private ArrayList<CheckBoxPreference> cbp_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        MyPreferenceFragment frag =  new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, frag).commit();


    }




    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }


    //settings fragment to display Setting UI
    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private ArrayList<CheckBoxPreference> cbp_list;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            cbp_list = new ArrayList<CheckBoxPreference>();
            cbp_list.add((CheckBoxPreference) getPreferenceManager()
                    .findPreference("popularity"));
            cbp_list.add((CheckBoxPreference) getPreferenceManager()
                    .findPreference("vote_average"));

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            CheckBoxPreference check = (CheckBoxPreference) findPreference(key);

            for (CheckBoxPreference cbp : cbp_list) {

                if (!cbp.getKey().equals(check.getKey()) && cbp.isChecked()) {
                    cbp.setChecked(false);
                }

            }


        }
    }

}
