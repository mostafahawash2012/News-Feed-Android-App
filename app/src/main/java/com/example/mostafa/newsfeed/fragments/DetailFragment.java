package com.example.mostafa.newsfeed.fragments;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.activities.MainActivity;
import com.example.mostafa.newsfeed.content.NewsContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER = 0;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    ImageView mImageView;
    TextView mTitle,mSite,mDate,mText;
    String mUrl;
    FloatingActionButton mFAB;
    Uri selectedUrl;
    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean favorite;
    private int mId=-1;
    private static Toast mToast;
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){// Tablet case
            selectedUrl = bundle.getParcelable("URI");
        }
        //phone case
        if (getActivity().getClass().getSimpleName().equals("DetailActivity")) {
            selectedUrl = getActivity().getIntent().getData();
        }
        Log.d(LOG_TAG,"OncreateView selectedUrl = "+ selectedUrl.toString());


        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!MainActivity.tablet);
       // mToolbar.inflateMenu(R.menu.detail);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        mImageView = (ImageView) rootView.findViewById(R.id.image_toolbar);
        mTitle = (TextView)rootView.findViewById(R.id.title_detail);
        mSite = (TextView)rootView.findViewById(R.id.site_detail);
        mDate = (TextView)rootView.findViewById(R.id.date_detail);
        mText = (TextView)rootView.findViewById(R.id.content);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,mUrl);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        mToast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onPrepareOptionsMenu");
        if(favorite) menu.findItem(R.id.addToFavorite).setIcon(R.drawable.ic_favorite_black_24dp);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        switch (id){
            case R.id.addToFavorite:{
                ContentValues values = new ContentValues();
                ContentResolver contentResolver = getActivity().getContentResolver();
                if (favorite) {//remove from fav
                    Log.d(LOG_TAG, "remove from fav");
                    values.put(NewsContract.NewsEntry.COLUMN_FAV,0);
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
               //     displayToast(getString(R.string.fav_added));
                    favorite=false;
                }else{//add to fav
                    Log.d(LOG_TAG, "add to fav");
                    values.put(NewsContract.NewsEntry.COLUMN_FAV, 1);
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                 //   displayToast(getString(R.string.fav_removed));
                    favorite=true;
                }
                contentResolver.update(NewsContract.NewsEntry.getUriWithId(mId),
                        values, null, null);
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated");
        getLoaderManager().initLoader(CURSOR_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(selectedUrl!= null){
            Log.d(LOG_TAG,"onCreateLoader");
            return new CursorLoader(getActivity(),selectedUrl,
                    null,null,null,null);
        }else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            Log.d(LOG_TAG, "onLoadFinished");
            mUrl = data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_URL));
            mTitle.setText(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
            mSite.setText(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_SITE)));
            mDate.setText(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_DATE)));
            mText.setText(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TEXT)));
            if(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL))!= null &&
                    data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL)).length()>1){
                Picasso.with(getActivity()).load(
                        data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL)))
                        .fit()
                        .into(mImageView);
            }else{
                mImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.news_default));
            }
            mCollapsingToolbarLayout.setTitle(data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
            mId = data.getInt(data.getColumnIndex(NewsContract.NewsEntry._ID));
            favorite = data.getInt(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_FAV))==1;
            getActivity().supportInvalidateOptionsMenu();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG," OnLoaderReset");
        getLoaderManager().restartLoader(CURSOR_LOADER, null, this);
    }
    private void displayToast(String message) {
        mToast.setText(message);
        mToast.show();
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(CURSOR_LOADER, null, this);
    }

}
