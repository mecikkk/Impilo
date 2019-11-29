package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.PersonalData
import com.met.impilo.utils.Constants

class FirebaseDataRepository {

    private val TAG = javaClass.simpleName

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        fun newInstance() = FirebaseDataRepository()
    }

    fun isUserDataExist(reference: String, exist: (String) -> Unit) {
        firestore.collection(reference)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty)
                    exist(it.documents[0].id)
                else
                    exist("")
            }
            .addOnFailureListener { exist("") }
    }

    fun pushData(reference: String, data: Any, success: (Boolean) -> Unit) {

        isUserDataExist(reference) { it ->
            if (it.isNotEmpty()) {
                // Override data
                firestore.collection(reference).document(it)
                    .set(data)
                    .addOnSuccessListener {
                        success(true)
                        Log.i(TAG, "Data added correctly")
                    }
                    .addOnFailureListener {
                        success(false)
                        Log.e(TAG, "Data adding error : " + it.cause + " | " + it.message)
                    }
            } else {
                // Add new data
                firestore.collection(reference)
                    .add(data)
                    .addOnSuccessListener {
                        success(true)
                        Log.i(TAG, "Data added correctly")
                    }
                    .addOnFailureListener {
                        success(false)
                        Log.e(TAG, "Data adding error : " + it.cause + " | " + it.message)
                    }
            }
        }
    }

    fun getData(reference: String, data: (List<DocumentSnapshot>?) -> Unit) {
        firestore.collection(reference)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty) {
                    data(it.documents)
                    Log.i(TAG, "Data downloaded correctly")
                }else {
                    data(null)
                    Log.e(TAG, "No data found")
                }
            }
            .addOnFailureListener {
                data(null)
                Log.e(TAG, "No data found")
            }
    }

    fun isConfigurationFinished(completed: (Boolean?) -> Unit){
        firestore.collection(Constants.REF_PERSONAL_DATA)
            .whereEqualTo("uid", FirebaseAuth.getInstance().uid)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty)
                    completed(it.documents[0].toObject(PersonalData::class.java)?.baseConfigurationCompleted ?: false)
                else
                    completed(false)
            }
            .addOnFailureListener { completed(false)}
    }

    fun setConfigurationFinished(success: (Boolean) -> Unit) {
        isUserDataExist(Constants.REF_PERSONAL_DATA) { documentId ->
            if(documentId.isNotEmpty())
                firestore.collection(Constants.REF_PERSONAL_DATA)
                    .document(documentId)
                    .update("baseConfigurationCompleted", true)
                    .addOnSuccessListener {
                        Log.i(TAG, "ConfigurationFinished  = true")
                        success(true)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "ConfigurationFinished update error : " + it.cause + " | " + it.message)
                        success(false)

                    }
        }
    }

}