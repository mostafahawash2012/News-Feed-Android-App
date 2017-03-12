package com.example.mostafa.newsfeed.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    SettingsFragment mSettingsFragment = new SettingsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,mSettingsFragment).commit();

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SettingsFragment.REQUEST_CHECK_SETTINGS){
            mSettingsFragment.onActivityResult(requestCode,resultCode,data);
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
