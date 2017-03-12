package com.example.mostafa.newsfeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.newsfeed.activities.DetailActivity;
import com.example.mostafa.newsfeed.content.NewsContract;
import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.models.NewsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.mostafa.newsfeed.R.drawable.news_default;

/**
 * Created by mostafa on 2/28/17.
 */

public class RecyclerCursorAdapter extends RecyclerView.Adapter<RecyclerCursorAdapter.NewsViewHolder> {

    String LOG_TAG = RecyclerCursorAdapter.class.getSimpleName();
    CursorAdapter mCursorAdapter;
    Context mContext;

    public RecyclerCursorAdapter(Context context, Cursor c){
        mContext=context;

        mCursorAdapter = new CursorAdapter(mContext,c,0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card,parent,false);
                NewsViewHolder nvh = new NewsViewHolder(v,mContext,mCursorAdapter);
                v.setTag(nvh);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                Log.e(LOG_TAG , "bindView");

                NewsViewHolder viewHolder = (NewsViewHolder) view.getTag();
                viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(
                        NewsContract.NewsEntry.COLUMN_TITLE)));
                viewHolder.site.setText(cursor.getString(cursor.getColumnIndex(
                        NewsContract.NewsEntry.COLUMN_SITE)));
                viewHolder.date.setText(cursor.getString(cursor.getColumnIndex(
                        NewsContract.NewsEntry.COLUMN_DATE)));
                if(cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL))!= null &&
                        cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL)).length()>1){
                    Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(
                            NewsContract.NewsEntry.COLUMN_IMAGE_URL
                    ))).fit().into(viewHolder.image);
                }else{
                    viewHolder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.news_default));
                }
            }
        };
    }
    public void swap(Cursor c){
        mCursorAdapter.swapCursor(c);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerCursorAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //passing the inflater job to the cursor adapter
        View v = mCursorAdapter.newView(mContext,mCursorAdapter.getCursor(),parent);
        return new NewsViewHolder(v,mContext,mCursorAdapter);

    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        //passing the bind operation to the cursor adapter
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView,mContext,mCursorAdapter.getCursor());

    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public  class NewsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title,site,date;
        Context mContext;
        CursorAdapter mCursorAdapter;
        public NewsViewHolder(View itemView, Context context, CursorAdapter cursorAdapter) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.imageView);
            title=(TextView)itemView.findViewById(R.id.title);
            site=(TextView)itemView.findViewById(R.id.site);
            date = (TextView) itemView.findViewById(R.id.date);
            mContext = context;
            mCursorAdapter=cursorAdapter;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor =(Cursor) mCursorAdapter.getItem(getAdapterPosition());
                    long index = cursor.getLong(cursor.getColumnIndex(NewsContract.NewsEntry._ID));
                    //Toast.makeText(mContext,"Clicked "+index,Toast.LENGTH_SHORT).show();
                    Uri selectedUri= NewsContract.NewsEntry.getUriWithId(index);
                    Log.e("Clicked "," Clicked");
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    //Bundle bundle = new Bundle();
                   // bundle.putParcelable("URI",selectedUri);
                    intent.setData(selectedUri);
                    mContext.startActivity(intent);

                }
            });
        }

    }
}
