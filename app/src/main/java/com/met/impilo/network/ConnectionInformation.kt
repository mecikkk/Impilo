package com.met.impilo.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ConnectionInformation : ViewModel() {

    private val TAG = this.javaClass.simpleName

    var isConnected = MutableLiveData<Boolean>()

    fun connectionReceiver(connectivityManager: ConnectivityManager) : BroadcastReceiver {
        Log.e(TAG,"Checking connection")

        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
                isConnected.value = networkInfo?.isConnectedOrConnecting ?: false
                Log.e(TAG,"is connected : " + networkInfo?.isConnectedOrConnecting)
            }
        }
    }

}