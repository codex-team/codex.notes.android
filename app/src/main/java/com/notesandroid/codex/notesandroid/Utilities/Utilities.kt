package com.notesandroid.codex.notesandroid.Utilities

import android.Manifest
import android.content.Context
import android.support.v4.app.ActivityCompat
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager


/**
 * Created by AksCorp on 04.02.2018.
 */
class Utilities {

    companion object {
        fun isInternetConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null)
                return activeNetwork.isConnected; // WIFI connected
            else
                return false; // no info object implies no connectivity
        }
    }

}