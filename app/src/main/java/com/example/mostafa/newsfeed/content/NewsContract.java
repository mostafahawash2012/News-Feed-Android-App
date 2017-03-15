package com.example.mostafa.newsfeed.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by mostafa on 3/1/17.
 */

public class NewsContract {

    public static final String AUTHORITY="com.example.mostafa.newsfeed.content.NewsContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String NEWS_TABLE_PATH = "news";

    public static final class NewsEntry implements BaseColumns{

        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NEWS_ID = "news_id";
        public static final String COLUMN_URL="url";
        public static final String COLUMN_SITE= "site";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_IMAGE_URL="image_url";
        public static final String COLUMN_TEXT="text";
        public static final String COLUMN_GLOBAL="global";
        public static final String COLUMN_LOCAL="local";
        public static final String COLUMN_FAV="fav";

        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,NEWS_TABLE_PATH);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+AUTHORITY +"/"+NEWS_TABLE_PATH;
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+AUTHORITY+"/"+NEWS_TABLE_PATH;


        public static Uri getUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static final String[] WIDGET_COLUMNS = {
                _ID,
                COLUMN_DATE,
                COLUMN_TITLE
        };


    }
    public static final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE IF NOT EXISTS "+
            NewsEntry.TABLE_NAME+"("+
            NewsEntry._ID+" INTEGER PRIMARY KEY,"+
            NewsEntry.COLUMN_NEWS_ID+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_URL+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_SITE+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_TITLE+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_DATE+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_IMAGE_URL+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_TEXT+" TEXT NOT NULL,"+
            NewsEntry.COLUMN_GLOBAL+" INTEGER DEFAULT 0,"+
            NewsEntry.COLUMN_LOCAL+" INTEGER DEFAULT 0,"+
            NewsEntry.COLUMN_FAV+" INTEGER DEFAULT 0"+
            ");";
        //    " UNIQUE ("+NewsEntry.COLUMN_NEWS_ID+") ON CONFLICT REPLAC);";


}
