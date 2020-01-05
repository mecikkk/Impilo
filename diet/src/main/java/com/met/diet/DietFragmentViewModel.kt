package com.met.diet

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Demand
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealProduct
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.repository.FirebaseDataRepository
import com.met.impilo.utils.Constants
import com.met.impilo.utils.Utils
import java.util.*

class DietFragmentViewModel : ViewModel() {

    private val TAG = this.javaClass.simpleName
    val repository = FirebaseDataRepository.newInstance()
    var userMealSet: MutableLiveData<UserMealSet> = MutableLiveData()
    var mealsExist: MutableLiveData<Boolean> = MutableLiveData()
    var allMeals: MutableLiveData<List<Meal>> = MutableLiveData()

    lateinit var demand : Demand
    var percentageDemand : MutableLiveData<Demand> = MutableLiveData()
    var mealsSummary : MutableLiveData<MealsSummary> = MutableLiveData()

    fun getMyDemand(){
        repository.getDemand {
            demand = it
            percentageDemand.value = calculateDemand(demand)
        }
    }

    fun getMyMealsSummary(){
        repository.getMealsSummary(Utils.dateToId(Date())){
            mealsSummary.value = it

        }
    }

    fun getPercentegeDemand() : LiveData<Demand> = percentageDemand
    fun getMealsSummary() : LiveData<MealsSummary> = mealsSummary


    fun getAllMealsByDateId(dateId: String) {
        Log.e(TAG, "Getting meals started")
        repository.getMealsByDateId(dateId) {
                allMeals.value = it
                Log.e(TAG, "Founded meals data : $it")
        }
    }

    fun storeStringListSharedPreferences(sp : SharedPreferences, userMealSet: UserMealSet) {
        val mealsString = TextUtils.join(";", userMealSet.mealsSet!!.toList())

        val editor = sp.edit()
        editor.putString(Constants.REF_USER_MEAL_SET, mealsString)
        editor.apply()
    }

    fun getMealsSetFromSharedPreferences(sp : SharedPreferences) : List<Meal> {
        val set = sp.getString(Constants.REF_USER_MEAL_SET, "")
        val mealNamesList = set?.split(";")!!.toMutableList()
        Log.e("DIET", "SharedPref set : $set")

        val mealList = mutableListOf<Meal>()
        var i = 0

            mealNamesList.forEach {
                mealList.add(Meal(id = i, name = it))
                i++
            }

        return mealList.toList()
    }

    fun generateMeals(mealSet: UserMealSet): List<Meal> {
        val m: MutableList<Meal> = mutableListOf()
        var i = 0

        mealSet.mealsSet?.forEach {
            m.add(Meal(id = i, name = it))
            i++
        }

        return m
    }

    fun getUserMealSet(){
        repository.getUserMealSet {
            userMealSet.value = it
            Log.e(TAG, "Founded user meal set : $it")
        }
    }

    private fun calculateDemand(demand : Demand) : Demand {
        Log.e(TAG, "MyDemand : $demand")
        Log.e(TAG, "MealSummary : ${mealsSummary.value}")

        var caloriesPercent = 0
        var carboPercent = 0f
        var proteinsPercent = 0f
        var fatsPercent = 0f

        if(mealsSummary.value != null) {
            caloriesPercent = ((mealsSummary.value?.kcalSum!!.toFloat() / demand.calories.toFloat()) * 100).toInt()
            carboPercent = (mealsSummary.value?.carbohydratesSum!! / demand.carbohydares) * 100
            proteinsPercent = (mealsSummary.value?.proteinsSum!! / demand.proteins) * 100
            fatsPercent = (mealsSummary.value?.fatsSum!! / demand.fats) * 100
        }

        return Demand(caloriesPercent, carboPercent.toInt(), proteinsPercent.toInt(), fatsPercent.toInt())

    }

    fun updateMealProducts(dateId: String, mealId : String, mealProducts : List<MealProduct>, success : (Boolean) -> Unit) {
        repository.updateProductsInMeal(dateId, mealId, mealProducts) {
            success(it)
        }
    }

    fun getMealSet() : LiveData<UserMealSet> = userMealSet

    fun getAllMeals() : LiveData<List<Meal>> = allMeals
}