package com.kyoungae.ohnaejunggo.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.kyoungae.ohnaejunggo.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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

        fun <T : View> showSnackbarOfNetworkIssues(view: T) {
            if (!checkNetworkState(view.context)) {
                Snackbar.make(
                    view,
                    view.resources.getString(R.string.network_issue),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }

        fun <T : View> showSnackbarOfUnknownIssues(view: T) {
            if (!checkNetworkState(view.context)) {
                Snackbar.make(
                    view,
                    view.resources.getString(R.string.unknown_issue),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }

        /**
         * 천 단위 콤마
         */
        fun Long.makeCommaString(): String {
            val decimal = DecimalFormat("##,###,###")
            return decimal.format(this)
        }

        fun String.removeComma(): String {
            return this.replace(",", "")
        }

        fun closeKeyboard(view: View, context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getCurrentTime(): String{
            val currentTime = System.currentTimeMillis()
            val dataFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREAN).format(currentTime)
            return dataFormat
        }

        private const val TAG = "CommonUtil"
    }
}