package com.met.impilo.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ConnectionInformation : ViewModel() {

    private val TAG = this.javaClass.simpleName

    var isConnected = MutableLiveData<Boolean>()

    @Suppress("DEPRECATION")
    fun connectionReceiver(connectivityManager: ConnectivityManager) : BroadcastReceiver {

        return object : BroadcastReceiver() {
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            override fun onReceive(context: Context?, intent: Intent?) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(connectivityManager.activeNetwork != null) {
                        val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        isConnected.value = nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

                    } else
                        isConnected.value = false
                } else {
                    val networkInfo = connectivityManager.activeNetworkInfo
                    isConnected.value = networkInfo?.isConnectedOrConnecting ?: false
                }

            }
        }
    }

}