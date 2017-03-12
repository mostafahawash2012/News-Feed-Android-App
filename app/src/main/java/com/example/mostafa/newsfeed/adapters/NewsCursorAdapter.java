package com.example.mostafa.newsfeed.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.content.NewsContract;
import com.squareup.picasso.Picasso;

/**
 * Created by mostafa on 3/8/17.
 */

public class NewsCursorAdapter extends CursorAdapter {


    public NewsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card,parent,false);
        NewsViewHolder nvh = new NewsViewHolder(v);
        v.setTag(nvh);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        NewsViewHolder viewHolder = (NewsViewHolder) view.getTag();
        viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_TITLE)));
        viewHolder.site.setText(cursor.getString(cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_SITE)));
        viewHolder.date.setText(cursor.getString(cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_DATE)));
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(
                NewsContract.NewsEntry.COLUMN_IMAGE_URL
        ))).fit().into(viewHolder.image);

    }

    public class NewsViewHolder{

        ImageView image;
        TextView title,site,date;
         NewsViewHolder(View itemView) {
            image=(ImageView)itemView.findViewById(R.id.imageView);
            title=(TextView)itemView.findViewById(R.id.title);
            site=(TextView)itemView.findViewById(R.id.site);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
