package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Demand
import com.met.impilo.data.PersonalData
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealProduct
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Constants
import com.met.impilo.utils.Utils

class FirebaseDataRepository {

    private val TAG = javaClass.simpleName

    private val firestore = FirebaseFirestore.getInstance()

    // Only for tests
    //private lateinit var firestore : FirebaseFirestore

    companion object {
        fun newInstance() = FirebaseDataRepository()
    }

    fun addPersonalData(personalData: PersonalData, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(personalData.uid).set(personalData).addOnSuccessListener {
            success(true)
            Log.e(TAG, "PersonalData added correctly")
        }.addOnFailureListener {
            success(false)
            Log.e(TAG, "PersonalData adding error : " + it.cause + " | " + it.message)
        }
    }

    fun getPersonalData(personalData: (PersonalData?) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
            if (it != null)
                personalData(it.toObject(PersonalData::class.java))
            else
                personalData(null)
        }.addOnFailureListener {
            personalData(null)
            Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
        }
    }

    fun addBodyMeasurement(bodyMeasurements: BodyMeasurements, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(bodyMeasurements.uid).collection(Constants.REF_BODY_MEASUREMENTS)
            .document(Utils.dateToId(bodyMeasurements.measurementDate)).set(bodyMeasurements).addOnSuccessListener {
                success(true)
                Log.e(TAG, "BodyMeasurement added correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "BodyMeasurement adding error : " + it.cause + " | " + it.message)
            }
    }

    fun getBodyMeasurement(id: String, bodyMeasurements: (BodyMeasurements) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_BODY_MEASUREMENTS).document(id).get()
            .addOnSuccessListener {
                if (it != null) bodyMeasurements(it.toObject(BodyMeasurements::class.java)!!)
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun addDemand(demand: Demand, success: (Boolean) -> Unit){
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_DEMAND_COLLECTION)
            .document(Constants.REF_DEMAND).set(demand).addOnSuccessListener {
                success(true)
                Log.e(TAG, "Demand added correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Demand adding error : " + it.cause + " | " + it.message)
            }
    }

    fun getDemand(demand : (Demand) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_DEMAND_COLLECTION).document(Constants.REF_DEMAND).get()
            .addOnSuccessListener {
                if (it != null) demand(it.toObject(Demand::class.java) ?: Demand())
                Log.e(TAG, "Demand received : $it")
            }.addOnFailureListener {
                Log.e(TAG, "Getting Demand error : " + it.cause + " | " + it.message)
            }
    }

    fun addOrUpdateMealSet(mealSet: UserMealSet, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(Constants.REF_USER_MEAL_SET)
            .set(mealSet).addOnSuccessListener {
                success(true)
                Log.e(TAG, "MealSet added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "MealSet adding error : " + it.cause + " | " + it.message)
            }
    }

    fun getUserMealSet(userMealSet: (UserMealSet) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(Constants.REF_USER_MEAL_SET).get()
            .addOnSuccessListener {
                if (it != null)
                    userMealSet(it.toObject(UserMealSet::class.java)!!)
                Log.e(TAG, "MealSet received : $it")
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun setOrUpdateMealsByDateId(dateId: String, allMeals: List<Meal>, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId).set(allMeals)
            .addOnSuccessListener {
                success(true)
                Log.e(TAG, "Meals added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Meals adding error : " + it.cause + " | " + it.message)
            }
    }

    fun getMealByIdAndDateId(mealId: String, dateId: String, meal: (Meal?) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId)
            .collection(Constants.REF_MEALS).document(mealId).get().addOnSuccessListener {
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

    fun setOrUpdateMeal(mealId: String, dateId: String, meal: Meal, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId)
            .collection(Constants.REF_MEALS).document(mealId).set(meal).addOnSuccessListener {
                success(true)
                Log.e(TAG, "Meal info added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Adding meal info error : " + it.cause + " | " + it.message)
            }
    }

    fun getMealsCollectionReference(dateId: String) =
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId).collection(
            Constants.REF_MEALS)

    fun getMealsSummary(dateId: String, mealsSummary: (MealsSummary?) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId).get().addOnSuccessListener {
                if (it != null) mealsSummary(it.toObject(MealsSummary::class.java))
                else {
                    mealsSummary(null)
                    Log.e(TAG, "Meals summary for day \"$dateId\" not found")
                }
            }.addOnFailureListener {
                mealsSummary(null)
                Log.e(TAG, "Getting meals summary for day \"$dateId\" error : " + it.cause + " | " + it.message)
            }

    }

    //TODO : Edycja i usuwanie produktow z posilku + odliczanie makroskladnikow

    fun updateProductsInMeal(dateId: String, mealId: String, mealProducts : List<MealProduct>, success: (Boolean) -> Unit){
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId)
            .collection(Constants.REF_MEALS).document(mealId).update("mealProducts", mealProducts)
            .addOnSuccessListener {
                success(true)
                Log.e(TAG, "Meal products updated updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Meal products update error : " + it.cause + " | " + it.message)
            }
    }

    fun setOrUpdateMealSummary(dateId: String, mealsSummary: MealsSummary, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId).set(mealsSummary)
            .addOnSuccessListener {
                success(true)
                Log.e(TAG, "Meals summary added/updated correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "Adding meals summary info error : " + it.cause + " | " + it.message)
            }
    }

    fun getMealsByDateId(dateId: String, allMeals: (List<Meal>?) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).collection(Constants.REF_ALL_MEALS).document(dateId)
            .collection(Constants.REF_MEALS).get().addOnSuccessListener { all ->
                val meals: MutableList<Meal> = mutableListOf()
                all.forEach {
                    meals.add(it.toObject(Meal::class.java))
                }
                allMeals(meals)
            }.addOnFailureListener {
                allMeals(null)
            }
    }

    fun searchProductByName(query: String, result: (List<FoodProduct>) -> Unit) {
        val products = mutableListOf<FoodProduct>()
        firestore.collection(Constants.REF_PRODUCTS).whereGreaterThanOrEqualTo("name", query.capitalize()).whereLessThanOrEqualTo("name", "${query.capitalize()}\uf8ff").get()
            .addOnSuccessListener { all ->
                all.forEach {
                    val p = it.toObject(FoodProduct::class.java)
                    p.id = it.id
                    products.add(p)
                    //list.add(ProductSearchItem(it.id, it.getString("name")!!, it.getString("producer")!!))
                    Log.e(TAG, "Substring : $query | FOUND : ${it.getString("name")}")
                }
                result(products)
            }.addOnFailureListener {
                Log.e(TAG, "No data founded")
            }
    }

    fun getProductById(productId: String, product: (FoodProduct) -> Unit) {
        firestore.collection(Constants.REF_PRODUCTS).document(productId).get().addOnSuccessListener {
            product(it.toObject(FoodProduct::class.java)!!)
        }.addOnFailureListener {
            Log.e(TAG, "Getting Product error : " + it.cause + " | " + it.message)
        }
    }

    fun getProductByBarcode(barcode: String, product: (FoodProduct?) -> Unit) {
        firestore.collection(Constants.REF_PRODUCTS).whereEqualTo("barcode", barcode).get().addOnSuccessListener { all ->

            if (!all.isEmpty) all.forEach {
                product(it.toObject(FoodProduct::class.java))
            }
            else product(null)

        }.addOnFailureListener {
            Log.e(TAG, "Getting Product error : " + it.cause + " | " + it.message)
            product(null)
        }
    }


    fun hasUserCompletedConfiguration(completed: (Boolean?) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
            if (it != null) completed(it.toObject(PersonalData::class.java)?.baseConfigurationCompleted ?: false)
            else completed(false)
        }.addOnFailureListener { completed(false) }
    }

    fun setConfigurationCompleted(success: (Boolean) -> Unit) {

        firestore.collection(Constants.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).update("baseConfigurationCompleted", true).addOnSuccessListener {
            Log.i(TAG, "ConfigurationFinished  = true")
            success(true)
        }.addOnFailureListener {
            Log.e(TAG, "ConfigurationFinished update error : " + it.cause + " | " + it.message)
            success(false)
        }
    }

}