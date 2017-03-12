package com.example.mostafa.newsfeed.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.Utility;
import com.example.mostafa.newsfeed.content.NewsContract;
import com.example.mostafa.newsfeed.models.NewsModel;
import com.example.mostafa.newsfeed.models.WebhoseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by mostafa on 3/7/17.
 */

public class NewsSyncAdapter extends AbstractThreadedSyncAdapter {

    private String LOG_TAG = NewsSyncAdapter.class.getSimpleName();
    Context mContext;
    public static final int SYNC_INTERVAL = 60 * 60 * 24;//24 hours

    public NewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = getContext();
    }
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(createDummyAccount(context),NewsContract.AUTHORITY, bundle);//requestSync() invokes onPerforme method
    }
    public static Account createDummyAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);// Get an instance of the Android account manager
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {// If the password doesn't exist, the account doesn't exist and you gotta add it using addAccountExplicitly
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {//add the account without password oo data
                return null;
            }
        }
        return newAccount;
    }
    public static void initializeSyncAdapter(Context context) {//initializes the syncAdapter with the specified interval
        Log.e("NewsSyncAdapter  ", "initialize SyncAdapter");
        Account account = createDummyAccount(context);
        ContentResolver.setSyncAutomatically(account, NewsContract.AUTHORITY,true);//Set whether or not the provider is synced when it receives a network tickle.
        ContentResolver.addPeriodicSync(account,NewsContract.AUTHORITY,Bundle.EMPTY,SYNC_INTERVAL);//This schedules your sync adapter to run after a certain amount of time
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(LOG_TAG,"onPerformSync Called!");

        int deleted = deleteOldData();
        Log.e(LOG_TAG, "Delete Old Data " + deleted + " deleted");
        String location=null;
        // ------------------------------Building URL Connection -----------------------------

        // Uri builtUri=Uri.parse("https://webhose.io/search?token=09efcf63-1c83-47f4-b1ff-62a433a5a999&format=json&q=(site_type:news)&size=10&language=english&sort=relevancy&site_type=news");
        for(int i=1 ; i<3 ; i++){
            HttpURLConnection urlConnection=null;
            BufferedReader bufferedReader=null;
            String line;
            String jsonStr = null;
            StringBuilder lines = new StringBuilder();
            String type="";
            if(i==1){
                type = "global";
                location = WebhoseAPI.QUERY_VALUE_GLOBAL;
                Log.e(LOG_TAG, "Global Location = "+location);
            }else{
                location = Utility.getLocation(mContext);
                type = "local";
                Log.e(LOG_TAG, "Local Location = "+location);
            }
            try {
                Uri builtUri=Uri.parse(WebhoseAPI.BASE_URL).buildUpon()
                        .appendQueryParameter(WebhoseAPI.TOKEN_KEY,WebhoseAPI.API_KEY)
                        .appendQueryParameter(WebhoseAPI.FORMAT,WebhoseAPI.FORMAT_VALUE)
                        .appendQueryParameter(WebhoseAPI.QUERY, location)
                        .appendQueryParameter(WebhoseAPI.SIZE,WebhoseAPI.SIZE_VALUE)
                        .appendQueryParameter(WebhoseAPI.LANGUAGE,WebhoseAPI.LANGUAGE_ENGLISH_VALUE)
                        .appendQueryParameter(WebhoseAPI.SORT,WebhoseAPI.SORT_VALUE)
                        .appendQueryParameter(WebhoseAPI.SITE_TYPE,WebhoseAPI.SITE_TYPE_VALUE)
                        .build();
                Log.e(LOG_TAG,"Built URI " + builtUri.toString());
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return ;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null){
                    lines.append(line);
                }
                if(lines.length() == 0){
                    return ;
                }
                jsonStr = lines.toString();
                Log.e(LOG_TAG ,type+" ---- " + jsonStr);
                //***************************_DATABASE_*******
                fetchJsonAndSave(jsonStr,i);//------

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    private void fetchJsonAndSave(String jsonStr,int type_key){
        if(jsonStr != null){
            try {
                String type="";
                JSONObject jObjectAll = new JSONObject(jsonStr);
                // do we have an error?(404)
                JSONArray jArray = jObjectAll.getJSONArray(WebhoseAPI.POSTS);
                Vector<ContentValues> cvVector = new Vector<ContentValues>(jArray.length());

                for(int i=0 ; i<jArray.length() ; i++){
                    JSONObject jObject = jArray.getJSONObject(i);
                    JSONObject threadObject = jObject.getJSONObject(WebhoseAPI.THREAD);
                    NewsModel newsModel = new NewsModel(
                            threadObject.getString(WebhoseAPI.UUID),
                            threadObject.getString(WebhoseAPI.URL),
                            threadObject.getString(WebhoseAPI.SITE),
                            threadObject.getString(WebhoseAPI.TITLE),
                            threadObject.getString(WebhoseAPI.PUBLISHED).substring(0,10),
                            threadObject.getString(WebhoseAPI.MAIN_IMAGE),
                            jObject.getString(WebhoseAPI.TEXT));

                    ContentValues newsValues = new ContentValues();

                    newsValues.put(NewsContract.NewsEntry.COLUMN_NEWS_ID,newsModel.mId);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_URL,newsModel.mUrl);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_SITE,newsModel.mSite);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE,newsModel.mTitle);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_DATE,newsModel.mDate);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL,newsModel.mImagePath);
                    newsValues.put(NewsContract.NewsEntry.COLUMN_TEXT,newsModel.mText);
                    if(type_key==1){
                         type = "global";
                        newsValues.put(NewsContract.NewsEntry.COLUMN_GLOBAL,1);//global
                    }else if(type_key == 2){
                         type = "local";
                        newsValues.put(NewsContract.NewsEntry.COLUMN_LOCAL,1);//local
                    }
                    cvVector.add(newsValues);
                }
                int inserted =0;
                if(cvVector.size() > 0){
                    ContentValues[] cvArray = new ContentValues[cvVector.size()];
                    cvVector.toArray(cvArray);

                    //insert new data
                    inserted=mContext.getContentResolver().bulkInsert(
                            NewsContract.NewsEntry.CONTENT_URI
                            ,cvArray);
                }
                Log.e(LOG_TAG, "FetchWeatherTask Completed. " + inserted + " Inserted "+ type+"");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.e(LOG_TAG," json string is null!");
        }
    }
    private int deleteOldData(){
        //Delete old date except favourite ones
        return mContext.getContentResolver().delete(
                NewsContract.NewsEntry.CONTENT_URI
                , NewsContract.NewsEntry.COLUMN_FAV+" = ?"
                ,new String[]{String.valueOf(0)});
    }
}
