package com.met.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.Gender
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.Week
import com.met.impilo.repository.WorkoutsRepository
import com.met.impilo.utils.toId
import java.util.*

class WorkoutsFragmentViewModel : ViewModel() {

    private val workoutsRepository = WorkoutsRepository.newInstance(FirebaseFirestore.getInstance())
    private var registrationDate = MutableLiveData<Date>()
    private var trainingDay = MutableLiveData<TrainingDay>()
    private var trainingPlanInfo = MutableLiveData<TrainingPlanInfo>()
    private var gender = MutableLiveData<Gender>()
    private var completedWorkoutsDates = MutableLiveData<List<String>>()


    fun signOut(){
        workoutsRepository.signOut()
    }

    fun fetchRegistrationDate(){
        workoutsRepository.getRegistrationDate {
            registrationDate.value = it
        }
    }

    fun fetchTrainingDay(date: Date, currentWeek : Week){
        workoutsRepository.getTrainingDayByDateId(date.toId(), currentWeek){
            trainingDay.value = it
        }
    }

    fun fetchTrainingPlanInfo(date: Date){
        workoutsRepository.getTrainingPlanInfoByDateOrScheme(date.toId()) {
            trainingPlanInfo.value = it
        }
    }

    fun fetchGender(){
        workoutsRepository.getPersonalData {
            gender.value = it?.gender
        }
    }

    fun fetchCompletedWorkoutsDates(){
        workoutsRepository.getDatesIdsOfAllCompletedWorkouts {
            completedWorkoutsDates.value = it
        }
    }

    fun getRegistrationDate() : LiveData<Date> = registrationDate
    fun getTrainingDay() : LiveData<TrainingDay> = trainingDay
    fun getTrainingPlanInfo() : LiveData<TrainingPlanInfo> = trainingPlanInfo
    fun getGender() : LiveData<Gender> = gender
    fun getCompletedWorkoutsDates() : LiveData<List<String>> = completedWorkoutsDates
}