
package com.ragentek.service.payservice;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;
import android.provider.Settings;

public class SettingActivity extends PreferenceActivity {
    private static final String TAG = "MainActivity";
    private Preference mBusinessServerSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.server_setting);
        initPrefs();
    }

    private void initPrefs() {
        mBusinessServerSetting = findPreference("pref_key_set_business_server");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean checkStatus = ((CheckBoxPreference) mBusinessServerSetting).isChecked();
        Log.i(TAG, "onPreferenceTreeClick checkStatus= " + checkStatus);
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
