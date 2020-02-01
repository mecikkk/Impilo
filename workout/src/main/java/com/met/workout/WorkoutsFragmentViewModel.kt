package com.met.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Gender
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.Week
import com.met.impilo.repository.WorkoutsRepository
import com.met.impilo.utils.toId
import java.util.*

class WorkoutsFragmentViewModel : ViewModel() {

    private val workoutsRepository = WorkoutsRepository.newInstance()
    private var registrationDate = MutableLiveData<Date>()
    private var trainingDay = MutableLiveData<TrainingDay>()
    private var trainingPlanInfo = MutableLiveData<TrainingPlanInfo>()
    private var gender = MutableLiveData<Gender>()

    fun fetchRegistrationDate(){
        workoutsRepository.getRegistrationDate {
            registrationDate.value = it
        }
    }

    fun fetchTrainingDay(date: Date, dayOfWeek : Int, currentWeek : Week){
        workoutsRepository.getTrainingDayByDateId(date.toId(), dayOfWeek, currentWeek){
            trainingDay.value = it
        }
    }

    fun fetchTrainingPlanInfo(){
        workoutsRepository.getTrainingPlanInfo {
            trainingPlanInfo.value = it
        }
    }

    fun fetchGender(){
        workoutsRepository.getPersonalData {
            gender.value = it?.gender
        }
    }

    fun getCurrentWeekDay(calendar: Calendar) : Int {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            1 -> 6
            else -> 0
        }
    }

    fun getCurrentWeekDay(date: Date) : Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            1 -> 6
            else -> 0
        }
    }

    fun getRegistrationDate() : LiveData<Date> = registrationDate
    fun getTrainingDay() : LiveData<TrainingDay> = trainingDay
    fun getTrainingPlanInfo() : LiveData<TrainingPlanInfo> = trainingPlanInfo
    fun getGender() : LiveData<Gender> = gender
}