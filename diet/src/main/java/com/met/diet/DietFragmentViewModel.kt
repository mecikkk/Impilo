package com.met.diet

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.met.impilo.data.Demand
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.repository.DemandRepository
import com.met.impilo.repository.FoodProductsRepository
import com.met.impilo.repository.MealsRepository
import com.met.impilo.utils.Const
import com.met.impilo.utils.toId
import java.util.*

class DietFragmentViewModel : ViewModel() {

    private val TAG = this.javaClass.simpleName

    private val demandRepository = DemandRepository.newInstance()
    private val mealsRepository = MealsRepository.newInstance()
    private val foodProductRepository = FoodProductsRepository.newInstance()

    var userMealSet: MutableLiveData<UserMealSet> = MutableLiveData()
    var allMeals: MutableLiveData<List<Meal>> = MutableLiveData()

    var fullDemand: MutableLiveData<Demand> = MutableLiveData()
    var percentageDemand: MutableLiveData<Demand> = MutableLiveData()
    var mealsSummary: MutableLiveData<MealsSummary> = MutableLiveData()

    fun getMyDemand() {
        demandRepository.getDemand {
            fullDemand.value = it
        }
    }

    fun getCalculatedDemand(date: Date) {
        demandRepository.getDemandInPercentages(date) {
            percentageDemand.value = it
        }
        Log.e(TAG, "Percentage demand : ${percentageDemand.value}")
    }

    fun getMyMealsSummary(date: Date) {
        mealsRepository.getMealsSummary(date.toId()) {
            mealsSummary.value = it

        }
    }

    fun getFullDemand(): LiveData<Demand> = fullDemand
    fun getPercentageDemand(): LiveData<Demand> = percentageDemand
    fun getMealsSummary(): LiveData<MealsSummary> = mealsSummary


    fun getAllMealsByDateId(dateId: String) {
        Log.e(TAG, "Getting meals started")
        mealsRepository.getMealsByDateId(dateId) {
            allMeals.value = it
            Log.e(TAG, "Founded meals data : $it")
        }
    }

    fun saveMealsSetInSharedPreferences(sp: SharedPreferences, userMealSet: UserMealSet) {
        val mealsString = TextUtils.join(";", userMealSet.mealsSet!!.toList())

        val editor = sp.edit()
        editor.putString(Const.REF_USER_MEAL_SET, mealsString)
        editor.apply()
    }

    fun getMealsSetFromSharedPreferences(sp: SharedPreferences): List<Meal> {
        val set = sp.getString(Const.REF_USER_MEAL_SET, "")
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

    fun getUserMealSet() {
        mealsRepository.getUserMealSet {
            userMealSet.value = it
            Log.e(TAG, "Founded user meal set : $it")
        }
    }

    fun updateMealProduct(dateId: String, mealId: Int, productPosition: Int, foodProduct: FoodProduct, servingTypeUpdate: ServingType, updatedServingSize: Float,
                          success: (Boolean) -> Unit) {

        mealsRepository.updateMealProduct(dateId, mealId, allMeals.value!!.toMutableList(), productPosition, foodProduct, servingTypeUpdate, updatedServingSize) {
            success(it)
        }
    }

    fun removeMealProduct(dateId: String, mealId: Int, productPosition: Int, success: (Boolean) -> Unit) {
        mealsRepository.removeMealProduct(dateId, mealId, allMeals.value!!.toMutableList(), productPosition) {
            success(it)
        }
    }


    fun getProductById(productId: String, product: (FoodProduct) -> Unit) {
        foodProductRepository.getProductById(productId) {
            product(it)
        }
    }

    fun getMealSet(): LiveData<UserMealSet> = userMealSet

    fun getAllMeals(): LiveData<List<Meal>> = allMeals

}