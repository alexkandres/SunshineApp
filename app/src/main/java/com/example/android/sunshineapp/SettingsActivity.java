package com.example.android.sunshineapp;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    static final String LOCATION_KEY= "location";
    static final String UNIT_KEY= "units";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.		         // updated when the preference changes
       bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
       bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));

    }

    private void bindPreferenceSummaryToValue(Preference preference){
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        //calling the overridden method, to set a default value??
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    //onPreferenceChange is called whenever a change is made to a prefrence
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
            Toast.makeText(this, "Settings for unit changed", Toast.LENGTH_SHORT).show();
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
            Toast.makeText(this, "Settings for location changed", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
