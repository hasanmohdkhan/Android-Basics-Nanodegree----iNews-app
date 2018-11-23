package com.example.hasanzian.newsapp.activity;

import android.app.LoaderManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasanzian.newsapp.BuildConfig;
import com.example.hasanzian.newsapp.R;
import com.example.hasanzian.newsapp.adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.broadcast.ConnectivityBroadcast;
import com.example.hasanzian.newsapp.loaderUtils.NewsLoader;
import com.example.hasanzian.newsapp.notification.AppNotificationChannel;
import com.example.hasanzian.newsapp.notification.NotificationJobScheduler;
import com.example.hasanzian.newsapp.utils.Model;
import com.example.hasanzian.newsapp.utils.PaginationScrollListener;
import com.example.hasanzian.newsapp.utils.QueryUtils;
import com.example.hasanzian.newsapp.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Model>> {
    /**
     * Constant value for the News loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    int pageNumber = 1;
    RecyclerAdaptor mAdaptor;
    ConnectivityBroadcast connectivityBroadcast = new ConnectivityBroadcast();
    public JobScheduler mJobScheduler;
    LinearLayoutManager mLinearLayoutManager;
    private static final String LOG_TAG = NewsActivity.class.getName();
    private LoaderManager loaderManager;
    private static final String NEWS_REQ_URL = "https://content.guardianapis.com/search";
    private static final String QUARRY_FORMAT = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String QUARRY_SEARCH = "q";
    private static final String QUARRY_PAGE_SIZE = "page-size";
    private static final String QUARRY_API_KEY = "api-key";
    private static final String API_KEY_VALUE = BuildConfig.ApiKey;
    private static final String QUARRY_PAGE = "page";
    private static final String QUARRY_SHOW_TAGS = "show-tags";
    private static final String QUARRY_SHOW_FIELDS = "show-fields";
    private static final String THUMBNAIL_VALUE = "thumbnail";
    private static final String QUARRY_ORDER_BY = "order-by";
    private static final String QUARRY_CONTRIBUTOR = "contributor";
    @BindView(R.id.empty_view)
    public TextView mEmptyStateTextView;
    @BindView(R.id.footer)
    public ProgressBar footer; // when scrolling is done
    private List<Model> mList = new ArrayList<>();
    boolean deletedNormalList = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 50;
    @BindView(R.id.loading_indicator)
    public View indicator; // shown when 1st time load app is loaded
    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;
    private String query = "";
    public static final String TAG = "Notification Service";
    public static NotificationManagerCompat notificationManager;
    @BindView(R.id.main_screen)
    View mainScreen;
    private List<Model> searchResult = new ArrayList<>();
    private boolean isItSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppNotificationChannel.nightModeSettings(getApplicationContext())) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        footer.setVisibility(View.INVISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        notificationManager = NotificationManagerCompat.from(this);

        mAdaptor = new RecyclerAdaptor(mList);
        mRecyclerView.setAdapter(mAdaptor);

        mEmptyStateTextView.setTypeface(QueryUtils.headingFont(this));
        mEmptyStateTextView.setVisibility(View.VISIBLE);

        mainScreen.setBackgroundColor(getResources().getColor(R.color.colorPrimaryTheme));
        mEmptyStateTextView.setTextColor(getResources().getColor(R.color.TextPrimary));

        if (QueryUtils.isConnectedToNetwork(this)) {
            mEmptyStateTextView.setVisibility(View.GONE);
            loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            showNotConnected(getString(R.string.no_internet));
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
                    loaderManager.restartLoader(1, null, NewsActivity.this);
                    mAdaptor.notifyDataSetChanged();
                } else {
                    showNotConnected("No internet");
                    footer.setVisibility(View.GONE);
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

        //   QueryUtils.notificationTrigger(this);
        if (QueryUtils.showNotification(this)) {


            ComponentName componentName = new ComponentName(this, NotificationJobScheduler.class);
            JobInfo info = new JobInfo.
                    Builder(QueryUtils.JOB_ID, componentName).setPersisted(true).setPeriodic(QueryUtils.notificationDelay(this) * 60 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();

            mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = mJobScheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }

        } else {
            mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            mJobScheduler.cancel(QueryUtils.JOB_ID);
            Log.d(TAG, "Job cancelled");
        }

    }

    private void showNotConnected(String string) {
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setText(string);
        indicator.setVisibility(View.GONE);
    }

    @Override
    public Loader<List<Model>> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minPageSize = sharedPrefs.getString(getString(R.string.settings_min_page_key), getString(R.string.settings_min_page_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_REQ_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=json`
        uriBuilder.appendQueryParameter(QUARRY_SEARCH, query);
        uriBuilder.appendQueryParameter(QUARRY_FORMAT, FORMAT_VALUE);
        uriBuilder.appendQueryParameter(QUARRY_SHOW_TAGS, QUARRY_CONTRIBUTOR);
        uriBuilder.appendQueryParameter(QUARRY_SHOW_FIELDS, THUMBNAIL_VALUE);
        uriBuilder.appendQueryParameter(QUARRY_ORDER_BY, orderBy);
        uriBuilder.appendQueryParameter(QUARRY_PAGE_SIZE, minPageSize);
        uriBuilder.appendQueryParameter(QUARRY_PAGE, String.valueOf(pageNumber));
        uriBuilder.appendQueryParameter(QUARRY_API_KEY, API_KEY_VALUE);

        Log.d("URL", uriBuilder.toString());

        // Create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Model>> loader, List<Model> list) {

        if (list == null && mList.size() > 0) {
            Log.d("List return", "" + list.size() + " mlist" + mList.size());
            showNotConnected("Unable to connect !!!");
            mRecyclerView.setVisibility(View.GONE);
            return;
        } else if (list.isEmpty()) {
            Log.d("No data", "" + list);
            showNotConnected(getString(R.string.no_data_available));
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);

            if (isItSearch) {
                if (deletedNormalList) { //true
                    mList.clear();
                    deletedNormalList = false;
                }
                mList.addAll(list);
            } else {
                mList.addAll(list);  // list
            }

            // Hide loading indicator because the data has been loaded
            indicator.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
            mAdaptor.notifyDataSetChanged();

        }

    }

    @Override
    public void onLoaderReset(Loader<List<Model>> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                isItSearch = true;
                deletedNormalList = true;
                query = query + s;
                loaderManager.restartLoader(1, null, NewsActivity.this);
                Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityBroadcast, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityBroadcast);
    }
}
