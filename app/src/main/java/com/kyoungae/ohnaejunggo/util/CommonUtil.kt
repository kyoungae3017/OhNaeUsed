package com.kyoungae.ohnaejunggo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.kyoungae.ohnaejunggo.R

class CommonUtil {
    companion object {
        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(ConnectivityManager::class.java)
            val network: Network = connectivityManager.activeNetwork ?: return false
            val actNetwork: NetworkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        }

        fun <T: View> showSnackbarOfNetworkIssues(view: T) {
            if (!checkNetworkState(view.context)) {
                Snackbar.make(view, view.resources.getString(R.string.network_issue), Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        private const val TAG = "CommonUtil"
    }
}