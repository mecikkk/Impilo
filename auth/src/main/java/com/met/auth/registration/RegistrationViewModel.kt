package com.met.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.data.*
import com.met.impilo.repository.FirebaseDataRepository
import com.met.impilo.utils.Constants
import java.util.*

class RegistrationViewModel : ViewModel() {

    private val repository = FirebaseDataRepository.newInstance()
    private lateinit var personalData: PersonalData
    private var bodyMeasurements: BodyMeasurements = BodyMeasurements()
    private var registrationSuccess = MutableLiveData<Boolean>()

    fun sendBasicInformation(height: Int, weight: Float, waist: Float, gender: Gender, birthDate: Date? = null) {
        repository.getData(Constants.REF_PERSONAL_DATA) {
            if (!it.isNullOrEmpty()) {
                personalData = (it[0].toObject(PersonalData::class.java)!!)
                personalData.gender = gender
                if(birthDate != null)
                    personalData.birthDate = birthDate
            } else {
                personalData = PersonalData(uid = FirebaseAuth.getInstance().uid!!,
                    gender = gender,
                    fullName = FirebaseAuth.getInstance().currentUser?.displayName,
                    registrationDate = Date())
                personalData.birthDate = birthDate
            }
        }

        bodyMeasurements.height = height
        bodyMeasurements.weight = weight
        bodyMeasurements.waist = waist
        bodyMeasurements.measurementDate = Date()
        bodyMeasurements.uid = FirebaseAuth.getInstance().uid
    }

    fun sendSomatotype(somatotype: Somatotype) {
        personalData.somatotype = somatotype
    }

    fun sendWorkoutStyle(workoutStyle: WorkoutStyle, trainingsPerWeek: Int, trainingTime: Int) {
        personalData.workoutStyle = workoutStyle
        personalData.workoutQuantity = trainingsPerWeek
        personalData.workoutTime = trainingTime
    }

    fun sendLifestyle(lifestyle: Lifestyle, goal: Goal) {

        personalData.lifestyle = lifestyle
        personalData.goal = goal

        repository.pushData(Constants.REF_PERSONAL_DATA, personalData) { it_p ->

            if (it_p) repository.pushData(Constants.REF_BODY_MEASUREMENTS, bodyMeasurements) { it_push ->
                if (it_push) {
                    repository.setConfigurationFinished {
                        registrationSuccess.value = it
                    }
                } else registrationSuccess.value = false

            }
            else registrationSuccess.value = false
        }
    }

    fun getRegistrationSuccess(): LiveData<Boolean> = registrationSuccess


}