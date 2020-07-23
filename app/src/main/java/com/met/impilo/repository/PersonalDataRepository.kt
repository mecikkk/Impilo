package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.Gender
import com.met.impilo.data.Goal
import com.met.impilo.data.PersonalData
import com.met.impilo.utils.Const
import java.util.*

class PersonalDataRepository(override val firestore: FirebaseFirestore) : FirebaseRepository(firestore) {

    override val TAG = javaClass.simpleName
    override val uid: String = FirebaseAuth.getInstance().uid!!

    companion object {
        fun newInstance(firestore: FirebaseFirestore) = PersonalDataRepository(firestore)
    }

    fun setOrUpdatePersonalData(personalData: PersonalData, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(personalData.uid).set(personalData).addOnSuccessListener {
            success(true)
        }.addOnFailureListener {
            success(false)
            Log.e(TAG, "PersonalData adding error : " + it.cause + " | " + it.message)
        }
    }

    fun updateGoal(goal: Goal, success: (Boolean) -> Unit){
        firestore.collection(Const.REF_USER_DATA).document(uid).update("goal", goal).addOnSuccessListener {
            success(true)
        }.addOnFailureListener {
            success(false)
            Log.e(TAG, "Goal update error : " + it.cause + " | " + it.message)
        }
    }

    fun getOrInitializePersonalData(gender: Gender, birthDate: Date? = null, result: (PersonalData) -> Unit) {
        getPersonalData {
            if (it != null) {
                it.gender = gender
                if (birthDate != null) it.birthDate = birthDate

                result(it)
            } else {
                val personalData =
                    PersonalData(uid = FirebaseAuth.getInstance().uid!!, gender = gender, fullName = FirebaseAuth.getInstance().currentUser?.displayName, registrationDate = Date())

                if (birthDate != null) personalData.birthDate = birthDate

                result(personalData)
            }
        }
    }

    fun hasUserCompletedConfiguration(completed: (Boolean?) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
            if (it != null) completed(it.toObject(PersonalData::class.java)?.baseConfigurationCompleted ?: false)
            else completed(false)
        }.addOnFailureListener { completed(false) }
    }

    fun setConfigurationCompleted(success: (Boolean) -> Unit) {

        firestore.collection(Const.REF_USER_DATA).document(FirebaseAuth.getInstance().uid!!).update("baseConfigurationCompleted", true).addOnSuccessListener {
            success(true)
        }.addOnFailureListener {
            Log.e(TAG, "ConfigurationFinished update error : " + it.cause + " | " + it.message)
            success(false)
        }
    }
}
