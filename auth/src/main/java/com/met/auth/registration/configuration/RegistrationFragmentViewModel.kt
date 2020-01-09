package com.met.auth.registration.configuration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.data.PersonalData
import com.met.impilo.repository.AuthRepository
import com.met.impilo.repository.PersonalDataRepository
import com.met.impilo.utils.Utils
import java.util.*

class RegistrationFragmentViewModel : ViewModel() {

    private val authRepository = AuthRepository.newInstance()
    private val personalDataRepository = PersonalDataRepository.newInstance()
    var signUpSuccess: MutableLiveData<Boolean> = MutableLiveData()

    fun signUpWithEmail(email: String, pass: String, name: String? = "", birthDate: Date) {
        authRepository.signUpWithEmail(email = Utils.cutWhitespaces(email), pass = pass) { success ->
            if (success) personalDataRepository.setOrUpdatePersonalData(
                PersonalData(uid = FirebaseAuth.getInstance().uid!!, fullName = name, birthDate = birthDate, registrationDate = Date())) {
                signUpSuccess.value = it
            }
        }
    }

}
