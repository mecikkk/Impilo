package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Demand
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.repository.DemandRepository
import com.met.impilo.repository.MealsRepository
import com.met.impilo.utils.toId
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    private val demandRepository = DemandRepository.newInstance()
    private val mealsRepository = MealsRepository.newInstance()

    var demand : MutableLiveData<Demand> = MutableLiveData()
    var mealsSummary : MutableLiveData<MealsSummary> = MutableLiveData()

    fun getMyDemand(){
        demandRepository.getDemand {
            demand.value = it
        }
    }

    fun getMyMealsSummary(){
        mealsRepository.getMealsSummary(Date().toId()){
            mealsSummary.value = it
        }
    }

    fun getDemand() : LiveData<Demand> = demand
    fun getMealsSummary() : LiveData<MealsSummary> = mealsSummary
}