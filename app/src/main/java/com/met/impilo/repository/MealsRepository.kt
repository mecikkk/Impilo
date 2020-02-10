package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealProduct
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Const
import com.met.impilo.utils.Operation
import java.util.*

class MealsRepository(override val firestore: FirebaseFirestore) : FirebaseRepository(firestore) {

    override val TAG = javaClass.simpleName
    override val uid: String = FirebaseAuth.getInstance().uid!!
    private lateinit var allMeals: MutableList<Meal>

    companion object {
        @JvmStatic
        fun newInstance(firestore: FirebaseFirestore) = MealsRepository(firestore)

    }
//
//    init {
//        resetUid()
//    }

    fun getMealsByDateId(dateId: String, allMeals: (List<Meal>?) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_MEALS).document(dateId).collection(Const.REF_MEALS).get()
            .addOnSuccessListener { all ->
                val meals: MutableList<Meal> = mutableListOf()
                all.forEach {
                    meals.add(it.toObject(Meal::class.java))
                }
                allMeals(meals)
            }.addOnFailureListener {
                Log.e(TAG, "Getting meals by date error : ${it.cause} | ${it.message}")
                Log.e(TAG, "${it.printStackTrace()}")
                allMeals(null)
            }
    }

    fun addOrUpdateMealSet(mealSet: UserMealSet, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Const.REF_ALL_MEALS).document(Const.REF_USER_MEAL_SET)
            .set(mealSet).addOnSuccessListener {
                success(true)
                Log.i(TAG, "MealSet added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "MealSet adding error : " + it.cause + " | " + it.message)
            }
    }

    private fun setOrUpdateMeal(mealId: String, dateId: String, meal: Meal, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Const.REF_ALL_MEALS).document(dateId)
            .collection(Const.REF_MEALS).document(mealId).set(meal).addOnSuccessListener {
                success(true)
                Log.i(TAG, "Meal info added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Adding meal info error : " + it.cause + " | " + it.message)
            }
    }

    private fun setOrUpdateMealsSummary(dateId: String, mealsSummary: MealsSummary, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Const.REF_ALL_MEALS).document(dateId).set(mealsSummary)
            .addOnSuccessListener {
                success(true)
                Log.i(TAG, "Meals summary added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Adding meals summary info error : " + it.cause + " | " + it.message)
            }
    }

    private fun getMealByIdAndDateId(mealId: String, dateId: String, meal: (Meal?) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Const.REF_ALL_MEALS).document(dateId)
            .collection(Const.REF_MEALS).document(mealId).get().addOnSuccessListener {
                if (it != null) meal(it.toObject(Meal::class.java))
                else {
                    meal(null)
                    Log.e(TAG, "Meal with id : $mealId in day : $dateId not found")
                }
            }.addOnFailureListener {
                meal(null)
                Log.e(TAG, "Getting meal error : " + it.cause + " | " + it.message)
            }
    }

    fun addProductToMeal(dateId: String, foodProduct: FoodProduct, servingSize: Float, servingType: ServingType, mealId: Int, mealName: String, success: (Boolean) -> Unit) {
        val servingName = if (servingType == ServingType.PORTION) foodProduct.servingName
        else "gram"

        val mealProduct = MealProduct(productId = foodProduct.id, name = foodProduct.name, producer = foodProduct.producer, kcalSum = foodProduct.nutrients!!.energyKcal,
            proteinsSum = foodProduct.nutrients!!.protein, carbohydratesSum = foodProduct.nutrients!!.carbo, fatsSum = foodProduct.nutrients!!.fats, servingName = servingName,
            servingSize = servingSize)

        getMealByIdAndDateId(mealId.toString(), dateId) { mealResult ->
            if (mealResult != null) {
                Log.i(TAG, "Meal exist")
                Log.i(TAG, "SingleMealSummary before add product : $mealResult")
                mealResult.apply {
                    kcalSum += mealProduct.kcalSum
                    carbohydratesSum += mealProduct.carbohydratesSum
                    proteinsSum += mealProduct.proteinsSum
                    fatsSum += mealProduct.fatsSum
                    mealProducts.add(mealProduct)
                }
                Log.i(TAG, "SingleMealSummary after add product : $mealResult")
                setOrUpdateMeal(mealId.toString(), dateId, mealResult) { mealUpdateSuccess ->
                    if (mealUpdateSuccess) {
                        updateMealsSummary(dateId, mealProduct, Operation.ADD) {
                            success(it)
                        }
                    }
                }
            } else {
                Log.i(TAG, "Creating new meal")
                getUserMealSet { mealSet ->

                    Log.e(TAG, mealSet.toString())
                    val names = mealSet.mealsSet
                    val count = mealSet.mealsSet?.size
                    val batch = firestore.batch()

                    for (i in 0 until count!!) {

                        var meal: Meal
                        if (mealId == i) {
                            meal =
                                Meal(mealId, mealName, mealProduct.kcalSum, mealProduct.carbohydratesSum, mealProduct.proteinsSum, mealProduct.fatsSum, mutableListOf(mealProduct))
                            updateMealsSummary(dateId, mealProduct, Operation.ADD) {}
                        } else meal = Meal(id = i, name = names!![i])

                        Log.i(TAG, "Adding meal $i : ${names!![i]}")
                        batch.set(getMealsCollectionReference(dateId).document(i.toString()), meal)

                    }

                    batch.commit().addOnSuccessListener {
                        success(true)
                    }.addOnFailureListener {
                            it.printStackTrace()
                            success(false)
                        }
                }
            }
        }
    }

    fun removeMealProduct(dateId: String, mealId: Int, allMeals: MutableList<Meal>, productPosition: Int, success: (Boolean) -> Unit) {
        val meal: Meal = allMeals[mealId]
        val mealProducts: MutableList<MealProduct> = allMeals[mealId].mealProducts
        val deletingProduct = mealProducts[productPosition]
        Log.i(TAG, "Actual products : $mealProducts")
        mealProducts.removeAt(productPosition)
        Log.i(TAG, "After delete : $mealProducts")

        meal.apply {
            kcalSum -= deletingProduct.kcalSum
            carbohydratesSum -= deletingProduct.carbohydratesSum
            proteinsSum -= deletingProduct.proteinsSum
            fatsSum -= deletingProduct.fatsSum
        }

        updateAllMealProducts(dateId, mealId.toString(), mealProducts) { productUpdateSuccess ->
            if (productUpdateSuccess) {
                allMeals[mealId].mealProducts = mealProducts
                setOrUpdateMeal(mealId.toString(), dateId, meal) { mealUpdateSuccess ->
                    if (mealUpdateSuccess) {
                        updateMealsSummary(dateId, deletingProduct, Operation.REMOVE) {
                            success(it)
                        }
                    } else success(false)
                }
            } else {
                success(false)
            }

        }
    }

    fun updateMealProduct(dateId: String, mealId: Int, allMeals: MutableList<Meal>, productPosition: Int, foodProduct: FoodProduct, servingTypeUpdate: ServingType,
                          updatedServingSize: Float, success: (Boolean) -> Unit) {

        val updatedServingName = if (servingTypeUpdate == ServingType.PORTION) foodProduct.servingName
        else "gram"

        val meal: Meal = allMeals[mealId]
        val mealProducts: MutableList<MealProduct> = allMeals[mealId].mealProducts
        val editingProduct = mealProducts[productPosition]

        val kcalTmp = editingProduct.kcalSum
        val carboTmp = editingProduct.carbohydratesSum
        val proteinsTmp = editingProduct.proteinsSum
        val fatsTmp = editingProduct.fatsSum

        meal.apply {
            kcalSum += foodProduct.nutrients!!.energyKcal - editingProduct.kcalSum
            carbohydratesSum += foodProduct.nutrients!!.carbo - editingProduct.carbohydratesSum
            proteinsSum += foodProduct.nutrients!!.protein - editingProduct.proteinsSum
            fatsSum += foodProduct.nutrients!!.fats - editingProduct.fatsSum
        }

        editingProduct.apply {
            kcalSum = foodProduct.nutrients!!.energyKcal
            carbohydratesSum = foodProduct.nutrients!!.carbo
            proteinsSum = foodProduct.nutrients!!.protein
            fatsSum = foodProduct.nutrients!!.fats
            servingName = updatedServingName
            servingSize = updatedServingSize
        }

        mealProducts[productPosition] = editingProduct

        updateAllMealProducts(dateId, mealId.toString(), mealProducts) { productsUpdateSuccess ->
            Log.i(TAG, "Updating meal product result : $productsUpdateSuccess")

            if (productsUpdateSuccess) {

                allMeals[mealId].mealProducts = mealProducts
                setOrUpdateMeal(mealId.toString(), dateId, meal) { mealUpdateSuccess ->
                    Log.i(TAG, "Updating meal result : $mealUpdateSuccess")

                    if (mealUpdateSuccess) {
                        editingProduct.apply {
                            kcalSum -= kcalTmp
                            carbohydratesSum -= carboTmp
                            proteinsSum -= proteinsTmp
                            fatsSum -= fatsTmp
                        }

                        updateMealsSummary(dateId, editingProduct, Operation.UPDATE) {
                            success(it)
                        }
                    } else success(mealUpdateSuccess)
                }
            } else success(productsUpdateSuccess)


        }
    }

    private fun updateAllMealProducts(dateId: String, mealId: String, mealProducts: List<MealProduct>, success: (Boolean) -> Unit) {
        updateProductsInMeal(dateId, mealId, mealProducts) {
            success(it)
        }
    }

    private fun updateProductsInMeal(dateId: String, mealId: String, mealProducts: List<MealProduct>, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Const.REF_ALL_MEALS).document(dateId)
            .collection(Const.REF_MEALS).document(mealId).update("mealProducts", mealProducts).addOnSuccessListener {
                success(true)
                Log.i(TAG, "Meal products updated updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Meal products update error : " + it.cause + " | " + it.message)
            }
    }

    private fun updateMealsSummary(dateId: String, mealProduct: MealProduct, operation: Operation, success: (Boolean) -> Unit) {
        getMealsSummary(dateId) { mealSummary ->

            if (mealSummary != null) {
                when (operation) {
                    Operation.ADD, Operation.UPDATE -> mealSummary.apply {
                        kcalSum += mealProduct.kcalSum
                        carbohydratesSum += mealProduct.carbohydratesSum
                        proteinsSum += mealProduct.proteinsSum
                        fatsSum += mealProduct.fatsSum
                    }
                    Operation.REMOVE -> mealSummary.apply {
                        kcalSum -= mealProduct.kcalSum
                        carbohydratesSum -= mealProduct.carbohydratesSum
                        proteinsSum -= mealProduct.proteinsSum
                        fatsSum -= mealProduct.fatsSum
                    }
                }

                setOrUpdateMealsSummary(dateId, mealSummary) {
                    success(it)
                }
            } else {
                val summary = MealsSummary(Date(), mealProduct.kcalSum, mealProduct.carbohydratesSum, mealProduct.proteinsSum, mealProduct.fatsSum)
                setOrUpdateMealsSummary(dateId, summary) {
                    success(it)
                }
            }
        }

    }
}