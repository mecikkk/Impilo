package com.met.impilo.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Demand
import com.met.impilo.data.PersonalData
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Const
import java.util.*

abstract class FirebaseRepository(var bd: FirebaseFirestore) {

    abstract val TAG : String
    abstract val firestore : FirebaseFirestore
    abstract val uid : String

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }

    fun getPersonalData(personalData: (PersonalData?) -> Unit) {
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).get().addOnSuccessListener {
            if (it != null)
                personalData(it.toObject(PersonalData::class.java))
            else
                personalData(null)
        }.addOnFailureListener {
            personalData(null)
            Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
        }
    }

    fun getRegistrationDate(date: (Date) -> Unit ){
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).get()
            .addOnSuccessListener {
                if(it.exists()){
                    Log.i(TAG, "Received registration date : ${it["registrationDate"]!! as Timestamp}")
                    date((it["registrationDate"]!! as Timestamp).toDate())
                }
            }.addOnFailureListener {
                Log.e(TAG, "Getting registartion date error : ${it.message}")
            }
    }

    fun getLastBodyMeasurement(bodyMeasurements: (BodyMeasurements) -> Unit) {
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_BODY_MEASUREMENTS).orderBy("measurementDate").get()
            .addOnSuccessListener {

                Log.i(TAG, "BodyMeasurements size : ${it.documents.size}")
                val result = it.documents[it.documents.size-1]
                bodyMeasurements(result.toObject(BodyMeasurements::class.java) ?: BodyMeasurements())

            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun getAllBodyMeasurementsOrderedByDate(measurements : (MutableLiveData<MutableList<BodyMeasurements>>) -> Unit ){
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_BODY_MEASUREMENTS).orderBy("measurementDate").get()
            .addOnSuccessListener { querySnapshot ->
                val list = mutableListOf<BodyMeasurements>()
                querySnapshot.forEach {
                    list.add(it.toObject(BodyMeasurements::class.java))
                    Log.e(TAG, "${it.toObject(BodyMeasurements::class.java)}")
                }
                measurements(MutableLiveData(list))
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun getDemand(demand : (Demand) -> Unit) {
//        resetUid()
        Log.e(TAG, "uid : $uid")
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_DEMAND_COLLECTION).document(Const.REF_DEMAND).get()
            .addOnSuccessListener {
                if (it.exists()) demand(it.toObject(Demand::class.java)!!)
                Log.i(TAG, "Demand received : ${it.toObject(Demand::class.java)!!}")
            }.addOnFailureListener {
                Log.e(TAG, "Getting Demand error : " + it.cause + " | " + it.message)
            }
    }

    fun getUserMealSet(userMealSet: (UserMealSet) -> Unit) {
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_MEALS).document(Const.REF_USER_MEAL_SET).get()
            .addOnSuccessListener {
                if (it != null)
                    userMealSet(it.toObject(UserMealSet::class.java)!!)
                Log.i(TAG, "MealSet received : $it")
            }.addOnFailureListener {
                Log.e(TAG, "Getting PersonalData error : " + it.cause + " | " + it.message)
            }
    }

    fun getMealsCollectionReference(dateId: String) =
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_MEALS).document(dateId).collection(
            Const.REF_MEALS)

    fun getMealsSummary(dateId: String, mealsSummary: (MealsSummary?) -> Unit) {
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_MEALS).document(dateId).get().addOnSuccessListener {
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

    fun isWorkoutConfigurationCompleted(completed : (Boolean) -> Unit){
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).get()
            .addOnSuccessListener {
                if(it.exists() && it["configurationCompleted"]!! == true){
                    Log.i(TAG, "Configuration completed")
                    completed(true)
                } else {
                    Log.i(TAG, "Configuration uncompleted")
                    completed(false)
                }
            }
            .addOnFailureListener {
                Log.i(TAG, "Checking workout configuration error : ${it.message}")
                completed(false)
            }
    }


}