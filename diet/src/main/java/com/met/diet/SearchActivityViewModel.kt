package com.met.diet

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealProduct
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.repository.FirebaseDataRepository
import com.met.impilo.utils.Utils
import java.util.*

class SearchActivityViewModel : ViewModel() {

    val TAG = javaClass.simpleName
    private val repository = FirebaseDataRepository.newInstance()

    fun searchProduct(query: String, result: (List<FoodProduct>) -> Unit) {
        repository.searchProductByName(query) {
            result(it)
        }
    }

    fun getProductByBarcode(barcode: String, result: (FoodProduct?) -> Unit) {
        repository.getProductByBarcode(barcode) {
            result(it)
        }
    }


    fun addProductToMeal(foodProduct: FoodProduct, servingSize: Float, servingType: ServingType, mealId: Int, mealName: String, success: (Boolean) -> Unit) {
        val servingName = if (servingType == ServingType.PORTION) foodProduct.servingName
        else "gram"

        val mealProduct = MealProduct(productId = foodProduct.id, name = foodProduct.name, producer = foodProduct.producer, kcalSum = foodProduct.nutrients!!.energyKcal,
            proteinsSum = foodProduct.nutrients!!.protein, carbohydratesSum = foodProduct.nutrients!!.carbo, fatsSum = foodProduct.nutrients!!.fats, servingName = servingName,
            servingSize = servingSize)



        repository.getMealByIdAndDateId(mealId.toString(), Utils.dateToId(Date())) { mealResult ->
            val dateId = Utils.dateToId(Date())
            if (mealResult != null) {
                Log.e(TAG, "Meal exist")
                Log.e(TAG, "SingleMealSummary before add product : $mealResult")
                mealResult.apply {
                    kcalSum += mealProduct.kcalSum
                    carbohydratesSum += mealProduct.carbohydratesSum
                    proteinsSum += mealProduct.proteinsSum
                    fatsSum += mealProduct.fatsSum
                    mealProducts.add(mealProduct)
                }
                Log.e(TAG, "SingleMealSummary after add product : $mealResult")
                repository.setOrUpdateMeal(mealId.toString(), dateId, mealResult) { mealUpdateSuccess ->
                    if (mealUpdateSuccess) {
                        updateMealSummary(mealProduct) {
                            success(it)
                        }
                    }
                }
            } else {
                Log.e(TAG, "Creating new meal")
                repository.getUserMealSet { mealSet ->

                    Log.e(TAG, mealSet.toString())
                    val names = mealSet.mealsSet
                    val count = mealSet.mealsSet?.size
                    val batch = FirebaseFirestore.getInstance().batch()

                    for (i in 0 until count!!) {

                        var meal : Meal
                        if (mealId == i) {
                            meal = Meal(mealId, mealName, mealProduct.kcalSum, mealProduct.carbohydratesSum, mealProduct.proteinsSum, mealProduct.fatsSum, mutableListOf(mealProduct))
                            updateMealSummary(mealProduct){
                            }
                        } else
                            meal = Meal(id = i, name = names!![i])

                        Log.e(TAG, "Adding meal $i : ${names!![i]}")
                        batch.set(repository.getMealsCollectionReference(dateId).document(i.toString()), meal)

                    }

                    batch.commit()
                        .addOnSuccessListener {
                            success(true)
                        }.addOnFailureListener {
                            it.printStackTrace()
                            success(false)
                        }
                }
            }
        }
    }

    private fun updateMealSummary(product: MealProduct, success: (Boolean) -> Unit) {
        repository.getMealsSummary(Utils.dateToId(Date())) { mealsSummaryResult ->
            if (mealsSummaryResult != null) {
                Log.e(TAG, "AllMealsSummary before add product : $mealsSummaryResult")
                mealsSummaryResult.apply {
                    kcalSum += product.kcalSum
                    carbohydratesSum += product.carbohydratesSum
                    proteinsSum += product.proteinsSum
                    fatsSum += product.fatsSum
                }
                Log.e(TAG, "AllMealsSummary after add product : $mealsSummaryResult")

                repository.setOrUpdateMealSummary(Utils.dateToId(Date()), mealsSummaryResult) {
                    success(it)
                }
            } else {
                val summary = MealsSummary(Date(), product.kcalSum, product.carbohydratesSum, product.proteinsSum, product.fatsSum)
                repository.setOrUpdateMealSummary(Utils.dateToId(Date()), summary) {
                    success(it)
                }
            }
        }
    }
}