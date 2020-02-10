package com.met.impilo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.workouts.generator.WorkoutType
import com.met.impilo.repository.FirebaseRepository
import com.met.impilo.repository.WorkoutsRepository

class MainActivityViewModel : ViewModel() {

    private var isWorkoutConfigurationCompleted = MutableLiveData<Boolean>()
    private val repository = WorkoutsRepository.newInstance(FirebaseFirestore.getInstance())

    fun checkIsWorkoutConfigCompleted() {
        repository.isWorkoutConfigurationCompleted {
            isWorkoutConfigurationCompleted.value = it
        }
    }

    fun signOut(){
        repository.signOut()
    }

    fun isWorkoutConfigCompleted() : LiveData<Boolean> = isWorkoutConfigurationCompleted

}