package com.example.hasanzian.newsapp.activity;

import static com.example.hasanzian.newsapp.constants.NewsConstant.API_KEY_VALUE;
import static com.example.hasanzian.newsapp.constants.NewsConstant.FORMAT_VALUE;
import static com.example.hasanzian.newsapp.constants.NewsConstant.NEWS_REQ_URL;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_API_KEY;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_CONTRIBUTOR;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_FORMAT;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_ORDER_BY;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_PAGE;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_PAGE_SIZE;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SEARCH;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_FIELDS;
import static com.example.hasanzian.newsapp.constants.NewsConstant.QUARRY_SHOW_TAGS;
import static com.example.hasanzian.newsapp.constants.NewsConstant.THUMBNAIL_VALUE;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hasanzian.newsapp.R;
import com.example.hasanzian.newsapp.adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.broadcast.ConnectivityBroadcast;
import com.example.hasanzian.newsapp.databinding.ActivityMainBinding;
import com.example.hasanzian.newsapp.loaderUtils.NewsLoader;
import com.example.hasanzian.newsapp.notification.AppNotificationChannel;
import com.example.hasanzian.newsapp.notification.NotificationJobScheduler;
import com.example.hasanzian.newsapp.utils.Model;
import com.example.hasanzian.newsapp.utils.PaginationScrollListener;
import com.example.hasanzian.newsapp.utils.QueryUtils;
import com.example.hasanzian.newsapp.utils.RecyclerTouchListener;
import com.example.hasanzian.newsapp.utils.UriUtils;

import java.util.ArrayList;
import java.util.List;

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


    private List<Model> mList = new ArrayList<>();
    boolean deletedNormalList = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 50;
    private String query = "";
    public static final String TAG = "Notification Service";
    public static NotificationManagerCompat notificationManager;

    private List<Model> searchResult = new ArrayList<>();
    private boolean isItSearch = false;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppNotificationChannel.nightModeSettings(getApplicationContext())) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.footer.setVisibility(View.INVISIBLE);
        binding.recyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setLayoutManager(mLinearLayoutManager);
        notificationManager = NotificationManagerCompat.from(this);

        mAdaptor = new RecyclerAdaptor(mList);
        binding.recyclerView.setAdapter(mAdaptor);

        binding.emptyView.setTypeface(QueryUtils.headingFont(this));
        binding.emptyView.setVisibility(View.VISIBLE);

        binding.mainScreen.setBackgroundColor(getResources().getColor(R.color.colorPrimaryTheme));
        binding.emptyView.setTextColor(getResources().getColor(R.color.TextPrimary));

        if (QueryUtils.isConnectedToNetwork(this)) {
            binding.emptyView.setVisibility(View.GONE);
            loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            showNotConnected(getString(R.string.no_internet));
        }

        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplication(), binding.recyclerView, new RecyclerTouchListener.ClickListener() {
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
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                binding.footer.setVisibility(View.VISIBLE);
                boolean isConnected = QueryUtils.isConnectedToNetwork(getApplication());
                if (isConnected) {
                    binding.emptyView.setVisibility(View.INVISIBLE);
                    pageNumber += 1;
                    Log.d("Load More", "Current number: " + pageNumber);
                    Log.d("TAG", "RestartLoader on LoadMore");
                    loaderManager.restartLoader(1, null, NewsActivity.this);
                    mAdaptor.notifyDataSetChanged();
                } else {
                    showNotConnected("No internet");
                    binding.footer.setVisibility(View.GONE);
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
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.emptyView.setText(string);
        binding.loadingIndicator.setVisibility(View.GONE);
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

        String newsUri = UriUtils.getNewsUri(query, orderBy, minPageSize, pageNumber);

        Log.d("URL", newsUri);

        // Create a new loader for the given URL
        return new NewsLoader(this, newsUri);
    }

    @Override
    public void onLoadFinished(Loader<List<Model>> loader, List<Model> list) {

        if (list == null && mList.size() > 0) {
            Log.d("List return", "" + list.size() + " mlist" + mList.size());
            showNotConnected("Unable to connect !!!");
            binding.recyclerView.setVisibility(View.GONE);
            return;
        } else if (list.isEmpty()) {
            Log.d("No data", "" + list);
            showNotConnected(getString(R.string.no_data_available));
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);

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
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.footer.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.GONE);
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
                query = "";
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
