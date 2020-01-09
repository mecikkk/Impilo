package com.met.impilo.repository

import android.util.Log
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.utils.Constants
import com.met.impilo.utils.toId
import java.util.*

class BodyMeasurementsRepository : FirebaseRepository() {

    override val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = BodyMeasurementsRepository()
    }

    fun setOrUpdateBodyMeasurement(date: Date, bodyMeasurements: BodyMeasurements, success: (Boolean) -> Unit) {
        firestore.collection(Constants.REF_USER_DATA).document(uid).collection(Constants.REF_BODY_MEASUREMENTS)
            .document(date.toId()).set(bodyMeasurements).addOnSuccessListener {
                success(true)
                Log.e(TAG, "BodyMeasurement added correctly")
            }.addOnFailureListener {
                success(false)
                Log.e(TAG, "BodyMeasurement adding error : " + it.cause + " | " + it.message)
            }
    }

}