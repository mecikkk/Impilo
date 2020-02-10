package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.Demand
import com.met.impilo.data.Gender
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.TrainingSystem
import com.met.impilo.data.workouts.Week
import com.met.impilo.repository.BodyMeasurementsRepository
import com.met.impilo.repository.DemandRepository
import com.met.impilo.repository.MealsRepository
import com.met.impilo.repository.WorkoutsRepository
import com.met.impilo.utils.toId
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val demandRepository = DemandRepository.newInstance(firestore)
    private val mealsRepository = MealsRepository.newInstance(firestore)
    private val workoutsRepository = WorkoutsRepository.newInstance(firestore)
    private val measurementsRepository = BodyMeasurementsRepository.newInstance(firestore)

    private var gender = MutableLiveData<Gender>()
    private var demand : MutableLiveData<Demand> = MutableLiveData()
    private var mealsSummary : MutableLiveData<MealsSummary> = MutableLiveData()
    private var trainingDay = MutableLiveData<TrainingDay>()
    private var trainingPlanInfo = MutableLiveData<TrainingPlanInfo>()
    private var completedWorkouts = MutableLiveData<List<String>>()
    private var allWeights = MutableLiveData<List<Pair<Date, Float>>>()

    fun signOut(){
        demandRepository.signOut()
    }

    fun fetchDemand(){
        demandRepository.getDemand {
            demand.value = it
        }
    }

    fun fetchMealsSummary(){
        mealsRepository.getMealsSummary(Date().toId()){
            mealsSummary.value = it
        }
    }

    fun fetchGender(){
        mealsRepository.getPersonalData {
            gender.value = it?.gender
        }
    }

    fun fetchTrainingDay(trainingSystem: TrainingSystem){
        val currentDay = Date()
        var week = Week.A
        if(trainingSystem == TrainingSystem.AB) {
            val currentDayCalendar: Calendar = Calendar.getInstance()
            currentDayCalendar.time = currentDay

            week = if (currentDayCalendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) Week.B
            else Week.A
        }

        workoutsRepository.getTrainingDayByDateId(currentDay.toId(), week){
            trainingDay.value = it
        }
    }

    fun isWorkoutConfigurationCompleted(completed : (Boolean) -> Unit){
        workoutsRepository.isWorkoutConfigurationCompleted {
            completed(it)
        }
    }

    fun fetchCompletedWorkouts(){
        workoutsRepository.getDatesIdsOfAllCompletedWorkouts {
            completedWorkouts.value = it
        }
    }

    fun fetchAllWeights(){
        measurementsRepository.getAllWeights {
            allWeights.value = it
        }
    }

    fun fetchTrainingPlanInfo(){
        workoutsRepository.getTrainingPlanInfoByDateOrScheme(Date().toId()){
            trainingPlanInfo.value = it
        }
    }

    fun getMaxWeight(allWeights : List<Pair<Date, Float>>) : Float = workoutsRepository.getMaxWeight(allWeights)
    fun getMinWeight(allWeights : List<Pair<Date, Float>>) : Float = workoutsRepository.getMinWeight(allWeights)


    fun getDemand() : LiveData<Demand> = demand
    fun getMealsSummary() : LiveData<MealsSummary> = mealsSummary
    fun getGender() : LiveData<Gender> = gender
    fun getTrainingDay() : LiveData<TrainingDay> = trainingDay
    fun getCompletedWorkouts() : LiveData<List<String>> = completedWorkouts
    fun getAllWeights() : LiveData<List<Pair<Date, Float>>> = allWeights
    fun getTrainingPlanInfo() : LiveData<TrainingPlanInfo> = trainingPlanInfo
}