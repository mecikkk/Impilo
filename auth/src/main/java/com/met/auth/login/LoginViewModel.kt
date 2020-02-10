package com.met.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.repository.AuthRepository
import com.met.impilo.repository.PersonalDataRepository
import com.met.impilo.utils.Utils


class LoginViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var personalDataRepository : PersonalDataRepository
    private val authRepository = AuthRepository.newInstance()

    var signInWithEmailSuccess = MutableLiveData<Boolean>()
    var signInWithGoogleSuccess = MutableLiveData<Boolean>()
    var isGoogleAccountConfigured = MutableLiveData<Boolean>()


    fun signInWithEmail(email: String, pass: String) {
        authRepository.signInWithEmail(Utils.cutWhitespaces(email), Utils.cutWhitespaces(pass)) {
            signInWithEmailSuccess.value = it
            if(it)
                personalDataRepository = PersonalDataRepository.newInstance(firestore)
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        authRepository.signUpWithGoogle(account) { success ->

            signInWithGoogleSuccess.value = success

            if (success) {
                personalDataRepository = PersonalDataRepository.newInstance(firestore)
                personalDataRepository.hasUserCompletedConfiguration { success ->
                    isGoogleAccountConfigured.value = success
                }
            }
        }
    }

    fun isAccountConfigured(success: (Boolean) -> Unit) {
        personalDataRepository = PersonalDataRepository.newInstance(firestore)
        personalDataRepository.hasUserCompletedConfiguration { completed ->
            if(completed!!)
                personalDataRepository = PersonalDataRepository.newInstance(firestore)

            success(completed)
        }
    }
}