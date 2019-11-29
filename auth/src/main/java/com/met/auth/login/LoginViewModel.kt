package com.met.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.repository.FirebaseAuthRepository
import com.met.impilo.repository.FirebaseDataRepository
import com.met.impilo.utils.Utils


class LoginViewModel : ViewModel() {

    private val firebaseAuthRepository = FirebaseAuthRepository()
    private val firestore = FirebaseDataRepository()
    var signInWithEmailSuccess = MutableLiveData<Boolean>()
    var signInWithGoogleSuccess = MutableLiveData<Boolean>()
    var isGoogleAccountConfigured = MutableLiveData<Boolean>()

    fun signInWithEmail(email: String, pass: String) {
        firebaseAuthRepository.signInWithEmail(Utils.cutWhitespaces(email), Utils.cutWhitespaces(pass)) {
            signInWithEmailSuccess.value = it
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        firebaseAuthRepository.signUpWithGoogle(account) { success ->
            signInWithGoogleSuccess.value = success

            if (success) {
                firestore.isConfigurationFinished {
                    isGoogleAccountConfigured.value = it
                }
            }
        }
    }

    fun isAccountConfigured(success : (Boolean) -> Unit){
        firestore.isConfigurationFinished {
            success(it!!)
        }
    }
}