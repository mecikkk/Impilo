package com.met.diet

import androidx.lifecycle.ViewModel
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.repository.FoodProductsRepository
import com.met.impilo.repository.MealsRepository
import com.met.impilo.utils.toId
import java.util.*

class SearchActivityViewModel : ViewModel() {

    val TAG = javaClass.simpleName

    private val foodProductsRepository = FoodProductsRepository.newInstance()
    private val mealsRepository = MealsRepository.newInstance()

    fun searchProduct(query: String, result: (List<FoodProduct>) -> Unit) {
        foodProductsRepository.findProductByName(query) {
            result(it)
        }
    }

    fun getProductByBarcode(barcode: String, result: (FoodProduct?) -> Unit) {
        foodProductsRepository.getProductByBarcode(barcode) {
            result(it)
        }
    }


    fun addProductToMeal(date: Date, foodProduct: FoodProduct, servingSize: Float, servingType: ServingType, mealId: Int, mealName: String, success: (Boolean) -> Unit) {
        mealsRepository.addProductToMeal(date.toId(), foodProduct, servingSize, servingType, mealId, mealName) {
            success(it)
        }
    }


}