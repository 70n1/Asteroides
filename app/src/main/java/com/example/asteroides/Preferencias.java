package com.example.asteroides;

import android.app.Activity;
import android.os.Bundle;
//import android.preference.PreferenceActivity;

/**
 * Created by AMARTIN on 03/10/2016.
 */

//public class Preferencias extends PreferenceActivity {
public class Preferencias extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferencias);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}