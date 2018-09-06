package com.example.hasanzian.newsapp.LoaderUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.hasanzian.newsapp.NetworkUtils.HttpHandler;
import com.example.hasanzian.newsapp.Utils.Model;
import com.example.hasanzian.newsapp.Utils.QueryUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader Class
 */

public class NewsLoader extends AsyncTaskLoader<List<Model>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;
    private List<Model> list = new ArrayList<>();

    public NewsLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
        Log.d(LOG_TAG, "onStartLoading....");

    }

    @Override
    public List<Model> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground....");
        HttpHandler httpHandler = new HttpHandler();
        String jsonString = "";
        try {
            //getting Json String
            jsonString = httpHandler.makeHttpRequest(createUrl(mUrl));
            list = QueryUtils.extractNews(jsonString);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /* Returns new URL object from the given string URL.
     */
    private URL createUrl(String mUrl) {
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

}
