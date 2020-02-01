package com.met.workout.own_plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Gender
import com.met.impilo.data.WorkoutStyle
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.repository.WorkoutsRepository

class SearchActivityViewModel : ViewModel() {

    private val workoutsRepository = WorkoutsRepository.newInstance()
    private var exercises = MutableLiveData<List<Exercise>>()
    private var gender = MutableLiveData<Gender>()
    private var weight = MutableLiveData<Float>()
    private var workoutStyle = MutableLiveData<WorkoutStyle>()


    fun fetchExercises(muscleSetReference : String){
        workoutsRepository.getExercises(muscleSetReference){
            exercises.value = it
        }
    }

    fun getExercises() : LiveData<List<Exercise>> = exercises

    fun fetchGenderAndWorkoutStyle(){
        workoutsRepository.getPersonalData {
            gender.value = it?.gender
            workoutStyle.value = it?.workoutStyle
        }
    }

    fun getGender() : LiveData<Gender> = gender

    fun fetchWeight() {
        workoutsRepository.getLastBodyMeasurement {
            weight.value = it.weight
        }
    }

    fun getWeight() : LiveData<Float> = weight
    fun getWorkoutStyle() : LiveData<WorkoutStyle> = workoutStyle
}