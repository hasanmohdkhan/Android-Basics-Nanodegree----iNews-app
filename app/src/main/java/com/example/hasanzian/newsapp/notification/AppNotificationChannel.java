package com.example.hasanzian.newsapp.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppNotificationChannel extends Application {
    public static final String CHANNEL_1_ID = "Channel 1";
    public static final String CHANNEL_2_ID = "Channel 2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        //check for Api 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelOne = new NotificationChannel(CHANNEL_1_ID, "channel1", NotificationManager.IMPORTANCE_HIGH);
            channelOne.setDescription("Normal Notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelOne);

        }

    }
}
