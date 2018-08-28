package com.example.hasanzian.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hasanzian.newsapp.Adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.LoaderUtils.NewsLoader;
import com.example.hasanzian.newsapp.Utils.Model;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Model>> {
    /**
     * Constant value for the News loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    int pageNumber = 1;
    RecyclerAdaptor mAdaptor;
    LinearLayoutManager mLinearLayoutManager;
    private static final String LOG_TAG = MainActivity.class.getName();
    LoaderManager loaderManager;
    RecyclerView mRecyclerView;
    private String API_KEY = "&api-key=441da542-bd64-4060-b81c-eff647cb6f27";
    private String START_URL = "https://content.guardianapis.com/search?q=&format=json&show-tags=contributor&show-fields=starRating,headline,thumbnail,short-url&order-by=newest&page=";
    private String URL_API;
    private List<Model> mList = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdaptor = new RecyclerAdaptor(mList);

        TextView mEmptyStateTextView = findViewById(R.id.empty_view);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View indicator = findViewById(R.id.loading_indicator);
            indicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplication(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Model model = mList.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getArticleUrl()));
                startActivity(browserIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                pageNumber += 1;
                Log.d("Load More", "Current number: " + pageNumber);
                Log.d("TAG", "RestartLoader on LoadMore");
                URL_API = START_URL + pageNumber + API_KEY;
                loaderManager.restartLoader(1, null, MainActivity.this);
                mAdaptor.notifyDataSetChanged();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    @Override
    public Loader<List<Model>> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        URL_API = START_URL + pageNumber + API_KEY;
        // Create a new loader for the given URL
        return new NewsLoader(this, URL_API);
    }

    @Override
    public void onLoadFinished(Loader<List<Model>> loader, List<Model> list) {
        Log.d(LOG_TAG, "list" + list.size());
        mList = list;

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdaptor = new RecyclerAdaptor(mList);
        mRecyclerView.setAdapter(mAdaptor);
    }

    @Override
    public void onLoaderReset(Loader<List<Model>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }


}
