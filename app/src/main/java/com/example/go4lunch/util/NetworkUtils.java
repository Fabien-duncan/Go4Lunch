package com.example.go4lunch.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * Utility class for checking network connectivity.
 */
public class NetworkUtils {
     /**
     * Checks if the device has an active network connection.
     *
     * @param context The context used to access system services.
     * @return True if network is available and connected, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
