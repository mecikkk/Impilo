package com.met.auth.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.data.*
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.repository.FirebaseDataRepository
import java.util.*

class RegistrationViewModel : ViewModel() {

    private val TAG = javaClass.simpleName

    private val repository = FirebaseDataRepository.newInstance()
    lateinit var personalData: PersonalData
    var bodyMeasurements: BodyMeasurements = BodyMeasurements()
    var demand = Demand()
    var registrationSuccess = MutableLiveData<Boolean>()

    // TODO : Demand dziala, testy tez, teraz trzeba demanda wyslac do firebase i zaczac aktualizowac wykresy z progressem

    fun sendBasicInformation(height: Int, weight: Float, waist: Float, gender: Gender, birthDate: Date? = null) {
        repository.getPersonalData {
            if(it != null) {
                personalData = it
                personalData.gender = gender
                if(birthDate != null)
                    personalData.birthDate = birthDate
            } else{
                personalData = PersonalData(uid = FirebaseAuth.getInstance().uid!!,
                    gender = gender,
                    fullName = FirebaseAuth.getInstance().currentUser?.displayName,
                    registrationDate = Date())

                if(birthDate != null)
                personalData.birthDate = birthDate
            }
        }

        bodyMeasurements.height = height
        bodyMeasurements.weight = weight
        bodyMeasurements.waist = waist
        bodyMeasurements.measurementDate = Date()
        bodyMeasurements.uid = FirebaseAuth.getInstance().uid!!
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

        calculateDemand()

        repository.addPersonalData(personalData){ it_personal ->
            if(it_personal)
                repository.addBodyMeasurement(bodyMeasurements){ it_measurement ->
                    if(it_measurement){
                        repository.addDemand(demand){ it_demand ->
                            if(it_demand)
                                repository.setConfigurationCompleted {
                                    registrationSuccess.value = it
                                }
                            else
                                registrationSuccess.value = false
                        }
                    } else
                        registrationSuccess.value = false
                }
            else registrationSuccess.value = false
        }
    }

    fun getCaloricDemand() : Int {
        val bmr = calculateBmr()
        val tea = calculateTea(bmr)
        val neat = calculateNeat()
        val tef = calculateTfe(bmr, neat.toFloat(), tea)

        val goal = when(personalData.goal){
            Goal.FAT_LOSE -> -300
            Goal.MAINTAIN -> 0
            Goal.MUSCLE_GAIN -> 200
            else -> 0
        }

        return (bmr + tea + tef + neat + goal).toInt()
    }

    fun calculateBmr() : Float {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val actualYear = calendar.get(Calendar.YEAR)
        calendar.time = personalData.birthDate!!
        val birthYear = calendar.get(Calendar.YEAR)

        return when(personalData.gender) {
            Gender.MALE -> {
                //Log.e(TAG, "BMR = (9.99 * ${bodyMeasurements.weight}) + (6.25 * ${bodyMeasurements.height}) - (4.92 * ($actualYear - $birthYear)) + 5)")
                ((9.99f * bodyMeasurements.weight) + (6.25f * bodyMeasurements.height) - (4.92f * (actualYear - birthYear)) + 5)
            }
            Gender.FEMALE -> {
                //Log.e(TAG, "BMR = (9.99 * ${bodyMeasurements.weight}) + (6.25 * ${bodyMeasurements.height}) - (4.92 * ($actualYear - $birthYear)) - 161)")
                ((9.99f * bodyMeasurements.weight) + (6.25f * bodyMeasurements.height) - (4.92f * (actualYear - birthYear)) - 161)
            } else -> 0f
        }
    }

    fun calculateTea(bmr : Float) : Float {
        val kcalPerMinute = when(personalData.workoutStyle) {
            WorkoutStyle.LIGHT -> 6
            WorkoutStyle.MODERATE -> 8
            WorkoutStyle.HEAVY -> 10
            else -> 0
        }

        val epoc : Float = personalData.workoutQuantity * (0.07 * bmr).toFloat()
        val tea : Float = ((kcalPerMinute * (personalData.workoutTime * personalData.workoutQuantity)) + epoc)

        return tea/7
    }

    fun calculateNeat() : Int {
        return when(personalData.somatotype) {
            Somatotype.ECTOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 700
                    Lifestyle.LIGHT -> 750
                    Lifestyle.ACTIVE -> 850
                    Lifestyle.VERY_ACTIVE -> 900
                    else -> 0
                }
            }
            Somatotype.ECTO_MESOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 600
                    Lifestyle.LIGHT -> 650
                    Lifestyle.ACTIVE -> 700
                    Lifestyle.VERY_ACTIVE -> 750
                    else -> 0
                }
            }
            Somatotype.MESO_ECTOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 500
                    Lifestyle.LIGHT -> 550
                    Lifestyle.ACTIVE -> 600
                    Lifestyle.VERY_ACTIVE -> 650
                    else -> 0
                }
            }
            Somatotype.MESOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 400
                    Lifestyle.LIGHT -> 450
                    Lifestyle.ACTIVE -> 450
                    Lifestyle.VERY_ACTIVE -> 500
                    else -> 0
                }
            }
            Somatotype.MESO_ENDOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 300
                    Lifestyle.LIGHT -> 350
                    Lifestyle.ACTIVE -> 400
                    Lifestyle.VERY_ACTIVE -> 450
                    else -> 0
                }
            }
            Somatotype.ENDO_MESOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 250
                    Lifestyle.LIGHT -> 300
                    Lifestyle.ACTIVE -> 350
                    Lifestyle.VERY_ACTIVE -> 450
                    else -> 0
                }
            }
            Somatotype.ENDOMORPH -> {
                when(personalData.lifestyle){
                    Lifestyle.SEDENTARY -> 200
                    Lifestyle.LIGHT -> 250
                    Lifestyle.ACTIVE -> 300
                    Lifestyle.VERY_ACTIVE -> 400
                    else -> 0
                }
            }
            else -> 0
        }
    }

    fun calculateTfe(bmr: Float, neat : Float, tea : Float) : Float = (0.08 * (bmr + tea + neat)).toFloat()

    fun calculateDemand(){
        demand.calories = getCaloricDemand()
        demand.proteins = (2*bodyMeasurements.weight).toInt()
        demand.fats = bodyMeasurements.weight.toInt()

        val carbohydratesCalories = (demand.calories - ((demand.proteins * 4) + (demand.fats * 9)))
        demand.carbohydares = (carbohydratesCalories / 4)
    }
    fun sendDefaultUserMealSet(userMealSet: UserMealSet){
        repository.addOrUpdateMealSet(userMealSet){
        }
    }

    fun getRegistrationSuccess(): LiveData<Boolean> = registrationSuccess


}