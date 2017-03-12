package com.example.mostafa.newsfeed.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.fragments.DetailFragment;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class DetailActivity extends AppCompatActivity {

    String LOG_TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"Oncreate");
        setContentView(R.layout.activity_detail);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.detailFragment_container, new DetailFragment()).commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
