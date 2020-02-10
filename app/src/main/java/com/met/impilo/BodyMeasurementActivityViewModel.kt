package com.met.impilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.repository.BodyMeasurementsRepository
import java.util.*

class BodyMeasurementActivityViewModel : ViewModel() {

    private val measurementsRepository = BodyMeasurementsRepository.newInstance(FirebaseFirestore.getInstance())

    private var lastMeasurements = MutableLiveData<BodyMeasurements>()
    private var separatedMeasurements = MutableLiveData<List<Pair<Date, List<Float>>>>()

    fun fetchSeparatedMeasurements() {
        measurementsRepository.getSeparatedMeasurements {
            separatedMeasurements.value = it
        }
    }

    fun fetchLastMeasurements() {
        measurementsRepository.getLastBodyMeasurement {
            lastMeasurements.value = it
        }
    }

    fun addNewMeasurements(bodyMeasurements: BodyMeasurements, succes : (Boolean) -> Unit){
        measurementsRepository.setOrUpdateBodyMeasurement(bodyMeasurements.measurementDate, bodyMeasurements){
            succes(it)
        }
    }

    fun getLastMeasurement(): LiveData<BodyMeasurements> = lastMeasurements
    fun getSeparatedValues(): LiveData<List<Pair<Date, List<Float>>>> = separatedMeasurements
}