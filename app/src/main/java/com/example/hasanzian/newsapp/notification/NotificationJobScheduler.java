package com.example.hasanzian.newsapp.notification;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.example.hasanzian.newsapp.activity.NewsActivity;
import com.example.hasanzian.newsapp.networkUtils.HttpHandler;
import com.example.hasanzian.newsapp.utils.Model;
import com.example.hasanzian.newsapp.utils.QueryUtils;

import java.io.IOException;
import java.util.ArrayList;

public class NotificationJobScheduler extends JobService {
    public static final String TAG = "Notification Service";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job stared");
        doInBackground(params);
        return true;
    }

    private void doInBackground(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);

                    String mUrl = "https://content.guardianapis.com/search?q=&format=json&show-tags=contributor&show-fields=thumbnail&order-by=newest&page-size=10&page=1&api-key=441da542-bd64-4060-b81c-eff647cb6f27";
                    HttpHandler handler = new HttpHandler();
                    String jsonString = "";
                    try {
                        jsonString = handler.makeHttpRequest(QueryUtils.createUrl(mUrl));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Model> list = QueryUtils.extractNews(jsonString);
                    Log.d(TAG, list.get(i).getHeading());
                    QueryUtils.displayNotification(getApplicationContext(), NewsActivity.notificationManager, list.get(i).getHeading(), list.get(i).getSection());

                    if (jobCancelled) {
                        return;
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
