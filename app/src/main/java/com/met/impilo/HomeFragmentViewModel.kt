package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Demand
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.repository.FirebaseDataRepository
import com.met.impilo.utils.Utils
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    private val repository = FirebaseDataRepository.newInstance()
    var demand : MutableLiveData<Demand> = MutableLiveData()
    var mealsSummary : MutableLiveData<MealsSummary> = MutableLiveData()

    fun getMyDemand(){
        repository.getDemand {
            demand.value = it
        }
    }

    fun getMyMealsSummary(){
        repository.getMealsSummary(Utils.dateToId(Date())){
            mealsSummary.value = it
        }
    }

    fun getDemand() : LiveData<Demand> = demand
    fun getMealsSummary() : LiveData<MealsSummary> = mealsSummary
}