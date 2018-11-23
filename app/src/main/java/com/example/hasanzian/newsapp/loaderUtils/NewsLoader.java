package com.example.hasanzian.newsapp.loaderUtils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.hasanzian.newsapp.networkUtils.HttpHandler;
import com.example.hasanzian.newsapp.utils.Model;
import com.example.hasanzian.newsapp.utils.QueryUtils;

import java.io.IOException;
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
            jsonString = httpHandler.makeHttpRequest(QueryUtils.createUrl(mUrl));
            list = QueryUtils.extractNews(jsonString);
            Log.d("List", "" + list.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
