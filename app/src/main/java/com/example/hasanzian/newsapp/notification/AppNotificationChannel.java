package com.example.hasanzian.newsapp.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.hasanzian.newsapp.R;

public class AppNotificationChannel extends Application {
    public static final String CHANNEL_1_ID = "Channel 1";
    private static final AppNotificationChannel INSTANCE = new AppNotificationChannel();
    private boolean isNightModeEnabled = false;

    public static boolean nightModeSettings(Context mContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPrefs.getBoolean(mContext.getString(R.string.settings_night_mode_key), false);
    }

    public static AppNotificationChannel getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        isNightModeEnabled = nightModeSettings(this);

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

    public boolean isNightModeEnabled() {
        return isNightModeEnabled;
    }

    public void setIsNightModeEnabled(boolean isNightModeEnabled) {
        this.isNightModeEnabled = isNightModeEnabled;
    }
}
