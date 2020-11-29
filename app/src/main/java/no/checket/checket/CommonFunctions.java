package no.checket.checket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public abstract class CommonFunctions {

    public static boolean isConnected(Context context) {
        /* Some important notes regarding this function

            ConnectivityManager:getActiveNetworkInfo() is now deprecated as of API 29
            ConnectivityManager:getActiveNetwork(), which is not deprecated, requires API 23, this application aims for API 21

            Therefore, in order to support API 21 and 22 we need to also implement ConnectivityManager:getActiveNetworkInfo() when the user is on an older version of Android

        */

        boolean isConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // If the user is running Android Marshmallow (Android 6.0) or higher
            if (connectivityManager != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        isConnected = true;
                    }
                }
            }
        } else {
            // If the user is running an older version than Android 6.0
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE || activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        isConnected = true;
                    }
                }
            }
        }
        return isConnected;
    }

}
