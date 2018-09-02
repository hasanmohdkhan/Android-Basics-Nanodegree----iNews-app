package com.example.hasanzian.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hasanzian.newsapp.Adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.LoaderUtils.NewsLoader;
import com.example.hasanzian.newsapp.Utils.Model;
import com.example.hasanzian.newsapp.Utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.empty_view)
    TextView mEmptyStateTextView;
    private String START_URL = "https://content.guardianapis.com/search?q=&format=json&show-tags=contributor&show-fields=starRating,headline,thumbnail,short-url&order-by=newest&page=";
    private String URL_API;
    private List<Model> mList = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 50;
    @BindView(R.id.footer)
    ProgressBar footer; // when scrolling is done
    @BindView(R.id.loading_indicator)
    View indicator; // shown when 1st time load app is loaded
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private String API_KEY = "&api-key=" + BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        footer.setVisibility(View.INVISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdaptor = new RecyclerAdaptor(mList);
        mRecyclerView.setAdapter(mAdaptor);

        mEmptyStateTextView.setTypeface(QueryUtils.headingFont(this));
        mEmptyStateTextView.setVisibility(View.VISIBLE);

        if (QueryUtils.isConnectedToNetwork(this)) {
            mEmptyStateTextView.setVisibility(View.GONE);
            loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            showNotConnected();
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
                footer.setVisibility(View.VISIBLE);
                boolean isConnected = QueryUtils.isConnectedToNetwork(getApplication());
                if (isConnected) {
                    mEmptyStateTextView.setVisibility(View.INVISIBLE);
                    pageNumber += 1;
                    Log.d("Load More", "Current number: " + pageNumber);
                    Log.d("TAG", "RestartLoader on LoadMore");
                    URL_API = START_URL + pageNumber + API_KEY;
                    loaderManager.restartLoader(1, null, MainActivity.this);
                    mAdaptor.notifyDataSetChanged();
                } else {
                    showNotConnected();
                    mList.clear();
                    mAdaptor.notifyDataSetChanged();
                }


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

    private void showNotConnected() {
        indicator.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(R.string.no_internet);
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
        mList.addAll(list);  // list

        // Hide loading indicator because the data has been loaded
        indicator.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.GONE);
        mAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Model>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }


}
