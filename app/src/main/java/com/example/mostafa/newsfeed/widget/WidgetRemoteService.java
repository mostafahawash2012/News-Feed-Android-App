package com.example.mostafa.newsfeed.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.content.NewsContract;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by mostafa on 3/14/17.
 */

public class WidgetRemoteService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data=null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {//notifyAppWidgetViewDataChanged()
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission so that we can get the data from content ptovider
                final long identityToken= Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        NewsContract.NewsEntry.CONTENT_URI,
                        NewsContract.NewsEntry.WIDGET_COLUMNS,
                        NewsContract.NewsEntry.COLUMN_GLOBAL+" = ?"+" AND "+NewsContract.NewsEntry.COLUMN_FAV+" = ?",
                        new String[]{String.valueOf(1),String.valueOf(0)}, ""+NewsContract.NewsEntry._ID+" ASC"
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)){
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                views.setTextViewText(R.id.title_widget,data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
                views.setTextViewText(R.id.date_widget,data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_DATE)));


                final Intent fillInIntent = new Intent();
                fillInIntent.setData(NewsContract.NewsEntry.CONTENT_URI);
                views.setOnClickFillInIntent(R.id.widget_list_item_root,fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(NewsContract.NewsEntry._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
