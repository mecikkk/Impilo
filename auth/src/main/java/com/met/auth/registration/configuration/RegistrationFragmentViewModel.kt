package com.met.auth.registration.configuration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.repository.FirebaseAuthRepository
import com.met.impilo.utils.Utils
import java.util.*

class RegistrationFragmentViewModel : ViewModel() {

    private var firebaseAuthRepository = FirebaseAuthRepository()
    var signUpSuccess : MutableLiveData<Boolean> = MutableLiveData()

    fun signUpWithEmail(email : String, pass : String, name : String? = "", birthDate: Date){
        firebaseAuthRepository.signUpWithEmail(Utils.cutWhitespaces(email), pass, name, birthDate) {
            signUpSuccess.value = it
        }
    }

}
