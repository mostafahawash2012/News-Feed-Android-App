package com.example.mostafa.newsfeed.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URI;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.id;
import static android.R.attr.switchMinWidth;

/**
 * Created by mostafa on 3/1/17.
 */

public class NewsContentProvider extends ContentProvider {

    private static DbHelper mDbHelper;
    private Context mContext;

    //------------URI Matcher-------------------

    private static final UriMatcher URI_MATCHER;
    private static final int NEWS=100;// CONTENT://com.example...(Athority)../NEWS_GLOBAL
    private static final int NEWS_WITH_ID=103;

    static {
        URI_MATCHER= new UriMatcher(UriMatcher.NO_MATCH);//No_Match is the returned code when doesn't match
        URI_MATCHER.addURI(NewsContract.AUTHORITY,NewsContract.NEWS_TABLE_PATH,NEWS);
        URI_MATCHER.addURI(NewsContract.AUTHORITY,NewsContract.NEWS_TABLE_PATH +"/#",NEWS_WITH_ID);//# means number  and * means string
     }

    @Override
    public boolean onCreate() {//It runs on the UI thread when your app starts up , so it shouldnt have any CIUD operations, just create a reference to the dp
        mContext = getContext();
        mDbHelper = new DbHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {//from this method the cursor determines whether the returned uri would be type of DIR or ITEM - to determine the type of returnded cursor for each uri
        switch(URI_MATCHER.match(uri)){
            case NEWS://(DIR) returns multiple rows
                return NewsContract.NewsEntry.CONTENT_TYPE;
            case NEWS_WITH_ID:
                return NewsContract.NewsEntry.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor returnedCursor;
        switch (URI_MATCHER.match(uri)){

            case NEWS:{
                returnedCursor = db.query(NewsContract.NewsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case NEWS_WITH_ID:{
                long _id = Long.valueOf(uri.getLastPathSegment());
                returnedCursor = db.query(NewsContract.NewsEntry.TABLE_NAME,projection,""+ NewsContract.NewsEntry._ID + "=?",new String[]{String.valueOf(_id)},null,null,sortOrder);
                //the sent id paremerter is actually the index of the clicked mews in the arrayList the holds the movies + 1
                // since the saved news in the database are sorted the same sort of the arrayList that holds the news
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        returnedCursor.setNotificationUri(mContext.getContentResolver(), uri);//this causes the cursor to register a content observer to watch for changes that happend to that uri
        //this allow the content provider to easily tell the UI when th cursor changes
        return returnedCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnedUri;
        long _id;
        switch(URI_MATCHER.match(uri)){
            case NEWS:{
                _id=db.insert(NewsContract.NewsEntry.TABLE_NAME,null,values);
                if(_id > 0){
                    returnedUri = NewsContract.NewsEntry.getUriWithId(_id);
                }else {
                    throw new SQLiteException("Failed to insert into database");
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }

        }
        mContext.getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db= mDbHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch(URI_MATCHER.match(uri)){
            case NEWS:{
                rowsDeleted = db.delete(NewsContract.NewsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case NEWS_WITH_ID:{
                long _id = Long.valueOf(uri.getLastPathSegment());
                rowsDeleted=db.delete(NewsContract.NewsEntry.TABLE_NAME,
                        ""+ NewsContract.NewsEntry._ID+"= ?",new String[]{String.valueOf(_id)});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch(URI_MATCHER.match(uri)){
            case NEWS:{
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME,null,value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                mContext.getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows_affected;
        switch (URI_MATCHER.match(uri)){
            case NEWS_WITH_ID:{
                rows_affected = db.update(NewsContract.NewsEntry.TABLE_NAME
                ,values
                , NewsContract.NewsEntry._ID +" = ?"
                ,new String[]{uri.getLastPathSegment()});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return rows_affected;

    }
}
