package com.met.auth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.*
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.repository.*
import java.util.*

class RegistrationViewModel : ViewModel() {

    private val TAG = javaClass.simpleName

    private val firestore = FirebaseFirestore.getInstance()
    private val personalDataRepository = PersonalDataRepository.newInstance(firestore)
    private val demandRepository = DemandRepository.newInstance(firestore)
    private val bodyMeasurementsRepository = BodyMeasurementsRepository.newInstance(firestore)
    private val mealsRepository = MealsRepository.newInstance(firestore)

    private lateinit var personalData: PersonalData
    private var bodyMeasurements: BodyMeasurements = BodyMeasurements()

    var registrationSuccess = MutableLiveData<Boolean>()


    fun sendBasicInformation(height: Int, weight: Float, waist: Float, gender: Gender, birthDate: Date? = null) {

        personalDataRepository.getOrInitializePersonalData(gender, birthDate) {
            personalData = it
        }

        bodyMeasurements.apply {
            this.height = height
            this.weight = weight
            this.waist = waist
            this.measurementDate = Date()
            this.uid = FirebaseAuth.getInstance().uid!!
        }

    }

    fun sendSomatotype(somatotype: Somatotype) {
        personalData.somatotype = somatotype
    }

    fun sendWorkoutStyle(workoutStyle: WorkoutStyle, trainingsPerWeek: Int, trainingTime: Int) {
        personalData.apply {
            this.workoutStyle = workoutStyle
            workoutQuantity = trainingsPerWeek
            workoutTime = trainingTime
        }
    }

    fun sendLifestyle(lifestyle: Lifestyle, goal: Goal) {

        personalData.lifestyle = lifestyle
        personalData.goal = goal

        personalDataRepository.setOrUpdatePersonalData(personalData) { personalDataSuccess ->
            if (personalDataSuccess)
                bodyMeasurementsRepository.setOrUpdateBodyMeasurement(Date(), bodyMeasurements) { it_measurement ->
                if (it_measurement) {
                    demandRepository.setOrUpdateDemand(personalData, bodyMeasurements) { it_demand ->
                        if (it_demand) personalDataRepository.setConfigurationCompleted {
                            registrationSuccess.value = it
                        }
                        else registrationSuccess.value = false
                    }
                } else registrationSuccess.value = false
            }
            else registrationSuccess.value = false
        }
    }

    fun sendDefaultUserMealSet(userMealSet: UserMealSet) {
        mealsRepository.addOrUpdateMealSet(userMealSet) {}
    }

    fun getRegistrationSuccess(): LiveData<Boolean> = registrationSuccess


}