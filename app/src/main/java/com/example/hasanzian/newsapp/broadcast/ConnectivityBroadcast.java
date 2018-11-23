package com.example.hasanzian.newsapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class ConnectivityBroadcast extends BroadcastReceiver {
    public static boolean connectivityStatus(boolean networkStatus) {
        return networkStatus;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                    false);

            if (connectivityStatus(noConnectivity)) {
                Toast.makeText(context, "No Connectivity", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Connected to network", Toast.LENGTH_SHORT).show();
            }


        }

    }
}
