package com.apps.apene.quicktrade;

import android.preference.PreferenceFragment;
import android.os.Bundle;

public class UserPreferences extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }
}
