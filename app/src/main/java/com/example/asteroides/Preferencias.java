package com.example.asteroides;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by AMARTIN on 03/10/2016.
 */

public class Preferencias extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}