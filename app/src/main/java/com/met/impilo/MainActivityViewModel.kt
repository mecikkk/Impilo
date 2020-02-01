package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.repository.FirebaseRepository
import com.met.impilo.repository.WorkoutsRepository

class MainActivityViewModel : ViewModel() {

    private var isWorkoutConfigurationCompleted = MutableLiveData<Boolean>()
    private val repository = WorkoutsRepository.newInstance()

    fun checkIsWorkoutConfigCompleted() {
        repository.isWorkoutConfigurationCompleted {
            isWorkoutConfigurationCompleted.value = it
        }
    }

    fun isWorkoutConfigCompleted() : LiveData<Boolean> = isWorkoutConfigurationCompleted

}