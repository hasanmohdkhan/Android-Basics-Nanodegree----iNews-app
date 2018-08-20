package com.example.hasanzian.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.hasanzian.newsapp.Adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.Utils.Model;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Model>> {
    private static final String URL_API = "https://content.guardianapis.com/search?q=&format=json&show-tags=contributor&show-fields=starRating,headline,thumbnail,short-url&order-by=newest&api-key=441da542-bd64-4060-b81c-eff647cb6f27";
    private static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    RecyclerView mRecyclerView;
    List<Model> mList = new ArrayList<>();
    private RecyclerAdaptor mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        Log.d(LOG_TAG, "initLoader");

    }

    @Override
    public Loader<List<Model>> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        // Create a new loader for the given URL
        return new NewsLoader(this, URL_API);
    }

    @Override
    public void onLoadFinished(Loader<List<Model>> loader, List<Model> list) {
        Log.d(LOG_TAG, "list" + list.size());
        mAdaptor = new RecyclerAdaptor(list);
        mRecyclerView.setAdapter(mAdaptor);
    }

    @Override
    public void onLoaderReset(Loader<List<Model>> loader) {
        // Loader reset, so we can clear out our existing data.
        //  mAdapter.clear();
        Log.d(LOG_TAG, "onLoaderReset");
    }

}
