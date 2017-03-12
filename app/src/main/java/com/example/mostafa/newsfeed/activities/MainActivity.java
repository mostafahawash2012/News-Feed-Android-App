package com.example.mostafa.newsfeed.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.adapters.ViewPagerAdapter;
import com.example.mostafa.newsfeed.content.NewsContract;
import com.example.mostafa.newsfeed.fragments.FavFragment;
import com.example.mostafa.newsfeed.fragments.GlobalFragment;
import com.example.mostafa.newsfeed.fragments.LocalFragment;
import com.example.mostafa.newsfeed.sync.NewsSyncAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList=new ArrayList<>();
    private int[] mTabIcons = {
            R.drawable.ic_language_black_24dp,
            R.drawable.ic_location_on_black_24dp,
            R.drawable.ic_star_black_24dp};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG,"OnCreate");
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);//Set a Toolbar to act as the ActionBar for this Activity window.
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();


        Cursor countCursor =this.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                new String[] {"count(*) AS count"},
                null,
                null,
                null);
        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        Log.e(LOG_TAG, "Counting columns in db = "+count);
        if(count == 0){
            Log.e(LOG_TAG, "Calling syncImmediately");
            NewsSyncAdapter.syncImmediately(this);
        }else {
            Log.e(LOG_TAG, "Calling initializeSyncAdapter");
            NewsSyncAdapter.initializeSyncAdapter(this);
        }
    }
    private void setupViewPager(ViewPager viewPager){

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        mFragmentList.add(new GlobalFragment());
        mFragmentList.add(new LocalFragment());
        mFragmentList.add(new FavFragment());
        mFragmentTitleList.add("Global");
        mFragmentTitleList.add("Local");
        mFragmentTitleList.add("Favourites");
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mFragmentList,mFragmentTitleList);
        viewPager.setAdapter(mViewPagerAdapter);
    }
    private void setupTabIcons(){

        mTabLayout.getTabAt(0).setIcon(mTabIcons[0]);
        mTabLayout.getTabAt(1).setIcon(mTabIcons[1]);
        mTabLayout.getTabAt(2).setIcon(mTabIcons[2]);
    }
}
