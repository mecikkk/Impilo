package com.met.auth.login

import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.repository.FirebaseAuthRepository
import com.met.impilo.utils.Utils
import io.grpc.okhttp.internal.Util

class LoginViewModel : ViewModel() {

    private val firebaseAuthRepository = FirebaseAuthRepository()
    var signInSuccessful = MutableLiveData<Boolean>()
    var isConnectedToInternet = MutableLiveData<Boolean>()

    fun signInWithEmail(email : String, pass : String){
        firebaseAuthRepository.signInWithEmail(Utils.cutWhitespaces(email), Utils.cutWhitespaces(pass)) {
            signInSuccessful.value = it
        }
    }

    fun checkInternetConnection(connectivityManager: ConnectivityManager){

    }

}