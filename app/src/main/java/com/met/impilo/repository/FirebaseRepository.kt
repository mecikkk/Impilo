package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Demand
import com.met.impilo.data.PersonalData
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Constants
import com.met.impilo.utils.toId
import java.util.*

abstract class FirebaseRepository {

    abstract val TAG : String
    protected val firestore = FirebaseFirestore.getInstance()
    protected var uid : String = "123"

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            if(it.currentUser != null)
                uid = it.uid!!
        }
    }

    private fun resetUid(){
        if(FirebaseAuth.getInstance().currentUser != null)
            uid = FirebaseAuth.getInstance().uid!!
    }

    fun getPersonalData(personalData: (PersonalData?) -> Unit) {
        resetUid()
        firestore.collection(Constants.REF_USER_DATA).document(uid).get().addOnSuccessListener {
            if (it != null)
                personalData(it.toObject(PersonalData::class.java))
            else
                personalData(null)
        }.addOnFailureListener {
            personalData(null)
            Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
        }
    }

    fun getBodyMeasurement(date : Date, bodyMeasurements: (BodyMeasurements) -> Unit) {
        resetUid()
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_BODY_MEASUREMENTS).document(date.toId()).get()
            .addOnSuccessListener {
                bodyMeasurements(it.toObject(BodyMeasurements::class.java) ?: BodyMeasurements())
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun getDemand(demand : (Demand) -> Unit) {
        resetUid()
        Log.e(TAG, "uid : $uid")
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_DEMAND_COLLECTION).document(Constants.REF_DEMAND).get()
            .addOnSuccessListener {
                if (it != null) demand(it.toObject(Demand::class.java) ?: Demand())
                Log.e(TAG, "Demand received : $it")
            }.addOnFailureListener {
                Log.e(TAG, "Getting Demand error : " + it.cause + " | " + it.message)
            }
    }

    fun getUserMealSet(userMealSet: (UserMealSet) -> Unit) {
        resetUid()
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_ALL_MEALS).document(Constants.REF_USER_MEAL_SET).get()
            .addOnSuccessListener {
                if (it != null)
                    userMealSet(it.toObject(UserMealSet::class.java)!!)
                Log.e(TAG, "MealSet received : $it")
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun getMealsCollectionReference(dateId: String) =
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_ALL_MEALS).document(dateId).collection(
            Constants.REF_MEALS)

    fun getMealsSummary(dateId: String, mealsSummary: (MealsSummary?) -> Unit) {
        resetUid()
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_ALL_MEALS).document(dateId).get().addOnSuccessListener {
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
}