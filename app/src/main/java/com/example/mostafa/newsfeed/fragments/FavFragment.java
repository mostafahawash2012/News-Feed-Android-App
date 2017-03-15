package com.example.mostafa.newsfeed.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mostafa.newsfeed.content.NewsContract;
import com.example.mostafa.newsfeed.models.NewsModel;
import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.adapters.RecyclerCursorAdapter;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = FavFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerCursorAdapter mRecyclerCursorAdapter;
    private LinearLayoutManager mLlm;
    private static final int CURSOR_LOADER = 0;



    public FavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(LOG_TAG," OnCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.news_recycler_view, container, false);
        mRecyclerView =(RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerCursorAdapter =new RecyclerCursorAdapter(getActivity(),null);
        mRecyclerView.setAdapter(mRecyclerCursorAdapter);
        mLlm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLlm);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG," OnCreateLoader");
        return new CursorLoader(getActivity()
                , NewsContract.NewsEntry.CONTENT_URI
                ,null
                , NewsContract.NewsEntry.COLUMN_FAV+" = ?"
                ,new String[]{String.valueOf(1)}, ""+NewsContract.NewsEntry._ID+" ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG," OnLoadFinished datasize = " + data.getCount());
        mRecyclerCursorAdapter.swap(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG," OnLoaderReset");
        mRecyclerCursorAdapter.swap(null);// passing null to  clean up resources
    }
}
