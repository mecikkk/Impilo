package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.*
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.Week
import com.met.impilo.repository.BodyMeasurementsRepository
import com.met.impilo.repository.DemandRepository
import com.met.impilo.repository.PersonalDataRepository
import com.met.impilo.repository.WorkoutsRepository
import com.met.impilo.utils.toId
import java.util.*

class ProfileFragmentViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val demandRepository = DemandRepository.newInstance(firestore)
    private val workoutsRepository = WorkoutsRepository.newInstance(firestore)
    private val measurementsRepository = BodyMeasurementsRepository.newInstance(firestore)
    private val personalDataRepository = PersonalDataRepository.newInstance(firestore)

    private var bmi = MutableLiveData<Float>()
    private var bf = MutableLiveData<Float>()
    private var personalData = MutableLiveData<PersonalData>()
    private var demand : MutableLiveData<Demand> = MutableLiveData()
    private var allWeights = MutableLiveData<List<Pair<Date, Float>>>()
    private var lastMeasurements = MutableLiveData<BodyMeasurements>()


    var thumbPosition = MutableLiveData<Float>()
    var bodyRating = MutableLiveData<BodyRating>()

    fun signOut(){
        demandRepository.signOut()
    }

    fun fetchDemand(){
        demandRepository.getDemand {
            demand.value = it
        }
    }

    fun fetchPersonalData(){
        workoutsRepository.getPersonalData {
            personalData.value = it
        }
    }

    fun fetchAllWeights(){
        measurementsRepository.getAllWeights {
            allWeights.value = it
        }
    }

    fun fetchBmiAndBf(){
        measurementsRepository.getBMI {
            bmi.value = it
        }
        measurementsRepository.getBF {
            bf.value = it
        }
    }

    fun fetchLastMeasurements(){
        measurementsRepository.getLastBodyMeasurement {
            lastMeasurements.value = it
        }
    }

    fun fetchThumbPosition(bf : Float){
        measurementsRepository.getThumbPosition(bf){
            thumbPosition.value = it
        }
    }

    fun fetchBodyRating(bf : Float){
        measurementsRepository.getBodyRating(bf){
            bodyRating.value = it
        }
    }

    fun changeGoal(goal : Goal, success : (Boolean) -> Unit) {
        personalDataRepository.updateGoal(goal){
            success(it)
        }
    }

    fun getMaxWeight(allWeights : List<Pair<Date, Float>>) : Float = workoutsRepository.getMaxWeight(allWeights)
    fun getMinWeight(allWeights : List<Pair<Date, Float>>) : Float = workoutsRepository.getMinWeight(allWeights)

    fun getDemand() : LiveData<Demand> = demand
    fun getAllWeights() : LiveData<List<Pair<Date, Float>>> = allWeights
    fun getPersonalData() : LiveData<PersonalData> = personalData
    fun getBF() : LiveData<Float> = bf
    fun getBMI() : LiveData<Float> = bmi
    fun getLastMeasurement() : LiveData<BodyMeasurements> = lastMeasurements

    fun getThumbPosition() : LiveData<Float> = thumbPosition
    fun getBodyRating() : LiveData<BodyRating> = bodyRating

}