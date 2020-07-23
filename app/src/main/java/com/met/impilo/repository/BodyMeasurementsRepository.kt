package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.*
import com.met.impilo.utils.Const
import com.met.impilo.utils.toId
import java.math.RoundingMode
import java.util.*

class BodyMeasurementsRepository(override val firestore: FirebaseFirestore) : FirebaseRepository(firestore){

    override val TAG = javaClass.simpleName
    override val uid: String = FirebaseAuth.getInstance().uid!!

    companion object {
        fun newInstance(firestore: FirebaseFirestore) = BodyMeasurementsRepository(firestore)
    }

    fun setOrUpdateBodyMeasurement(date: Date, bodyMeasurements: BodyMeasurements, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_BODY_MEASUREMENTS)
            .document(date.toId()).set(bodyMeasurements).addOnSuccessListener {
                success(true)
            }.addOnFailureListener {
                success(false)
            }
    }

    fun getAllWeights(weights : (List<Pair<Date, Float>>) -> Unit){
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_BODY_MEASUREMENTS).get()
            .addOnSuccessListener { collection ->
                if(!collection.documents.isNullOrEmpty()){
                    val list = mutableListOf<Pair<Date, Float>>()
                    collection.documents.forEach {
                        val measurement : BodyMeasurements = it.toObject(BodyMeasurements::class.java)!!
                        list.add(Pair(measurement.measurementDate, measurement.weight))
                    }
                    weights(list.sortedBy { it.first })
                }
            }.addOnFailureListener {
                Log.i(TAG, "Getting weights error : ${it.message}")
            }
    }

    fun getSeparatedMeasurements(separatedMeasurements : (List<Pair<Date, List<Float>>>) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_BODY_MEASUREMENTS).get()
            .addOnSuccessListener { collection ->
                if(!collection.documents.isNullOrEmpty()){
                    val allMeasurements = mutableListOf<Pair<Date, List<Float>>>()
                    var list = mutableListOf<Float>()
                    collection.documents.forEach { doc ->
                        val measurement : BodyMeasurements = doc.toObject(BodyMeasurements::class.java)!!
                        list.add(measurement.weight)
                        list.add(measurement.waist)
                        list.add(measurement.bicep)
                        list.add(measurement.forearm)
                        list.add(measurement.chest)
                        list.add(measurement.shoulders)
                        list.add(measurement.neck)
                        list.add(measurement.hips)
                        list.add(measurement.thigh)
                        list.add(measurement.calves)
                        allMeasurements.add(Pair(measurement.measurementDate, list))
                        list = mutableListOf()
                    }
                    separatedMeasurements(allMeasurements)
                }
            }.addOnFailureListener {
                Log.i(TAG, "Getting weights error : ${it.message}")
            }
    }

    fun getBMI(bmi : (Float) -> Unit){
        getLastBodyMeasurement{
            val denominator = (it.height * it.height)/100f
            val result = ((it.weight/denominator)*100f).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
            bmi(result)
        }
    }

    fun getBF(bf : (Float) -> Unit){
        getPersonalData { personalData ->
            val x : Float = when(personalData?.gender) {
                Gender.MALE -> 98.42f
                Gender.FEMALE -> 76.76f
                else -> 0f
            }

            getLastBodyMeasurement {

                val a = (4.15f * it.waist)
                val b = (a/2.54f)
                val c = ((0.082f*it.weight) * 2.2f)
                val d = (b-c-x)
                val e = (it.weight * 2.2f)
                val result = ((d/e) * 100f).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toFloat()
                bf(result)
            }
        }
    }

    fun getThumbPosition(bf : Float, thumbPosition : (Float) -> Unit){
            getFatLevel(bf){
                thumbPosition(calculateThumbPosition(it))
        }
    }

    fun getBfInformationMap(map : (Map<Pair<Float, Float>, FatLevel>) -> Unit){
        val values : MutableList<Map<Pair<Float, Float>, FatLevel>> = mutableListOf()

        getPersonalData {
            when (it?.gender) {
                Gender.MALE -> {
                    values.add(linkedMapOf(Pair(Pair(0f, 14.9f), FatLevel.LOW), Pair(Pair(14.9f, 19f), FatLevel.NORMAL), Pair(Pair(19f, 23.3f), FatLevel.INCREASED),
                        Pair(Pair(23.3f, 100f), FatLevel.OBESITY))) /* age 19-24*/
                    values.add(linkedMapOf(Pair(Pair(0f, 16.5f), FatLevel.LOW), Pair(Pair(16.5f, 20.3f), FatLevel.NORMAL), Pair(Pair(20.3f, 24.4f), FatLevel.INCREASED),
                        Pair(Pair(24.4f, 100f), FatLevel.OBESITY))) /* age 25-29*/
                    values.add(linkedMapOf(Pair(Pair(0f, 18f), FatLevel.LOW), Pair(Pair(18f, 21.5f), FatLevel.NORMAL), Pair(Pair(21.5f, 25.2f), FatLevel.INCREASED),
                        Pair(Pair(25.2f, 100f), FatLevel.OBESITY))) /* age 30-34*/
                    values.add(linkedMapOf(Pair(Pair(0f, 19.4f), FatLevel.LOW), Pair(Pair(19.4f, 22.6f), FatLevel.NORMAL), Pair(Pair(22.6f, 26.1f), FatLevel.INCREASED),
                        Pair(Pair(26.1f, 100f), FatLevel.OBESITY))) /* age 35-39*/
                    values.add(linkedMapOf(Pair(Pair(0f, 20.5f), FatLevel.LOW), Pair(Pair(20.5f, 23.6f), FatLevel.NORMAL), Pair(Pair(23.6f, 26.9f), FatLevel.INCREASED),
                        Pair(Pair(26.9f, 100f), FatLevel.OBESITY))) /* age 40-44*/
                    values.add(linkedMapOf(Pair(Pair(0f, 21.5f), FatLevel.LOW), Pair(Pair(21.5f, 24.5f), FatLevel.NORMAL), Pair(Pair(24.5f, 27.6f), FatLevel.INCREASED),
                        Pair(Pair(27.6f, 100f), FatLevel.OBESITY))) /* age 45-49*/
                    values.add(linkedMapOf(Pair(Pair(0f, 22.7f), FatLevel.LOW), Pair(Pair(22.7f, 25.6f), FatLevel.NORMAL), Pair(Pair(25.6f, 28.7f), FatLevel.INCREASED),
                        Pair(Pair(28.7f, 100f), FatLevel.OBESITY))) /* age 50-54*/
                    values.add(linkedMapOf(Pair(Pair(0f, 23.2f), FatLevel.LOW), Pair(Pair(23.2f, 26.2f), FatLevel.NORMAL), Pair(Pair(26.2f, 29.3f), FatLevel.INCREASED),
                        Pair(Pair(29.3f, 100f), FatLevel.OBESITY))) /* age 55-59*/
                    values.add(linkedMapOf(Pair(Pair(0f, 23.5f), FatLevel.LOW), Pair(Pair(23.5f, 26.7f), FatLevel.NORMAL), Pair(Pair(26.7f, 29.8f), FatLevel.INCREASED),
                        Pair(Pair(29.8f, 100f), FatLevel.OBESITY))) /* age 60 +*/
                }
                Gender.FEMALE -> {
                    values.add(linkedMapOf(Pair(Pair(0f, 22f), FatLevel.LOW), Pair(Pair(22f, 25f), FatLevel.NORMAL), Pair(Pair(25f, 29.6f), FatLevel.INCREASED),
                        Pair(Pair(29.6f, 100f), FatLevel.OBESITY))) /* age 19-24*/
                    values.add(linkedMapOf(Pair(Pair(0f, 22.1f), FatLevel.LOW), Pair(Pair(22.1f, 25.4f), FatLevel.NORMAL), Pair(Pair(25.4f, 29.8f), FatLevel.INCREASED),
                        Pair(Pair(29.8f, 100f), FatLevel.OBESITY))) /* age 25-29*/
                    values.add(linkedMapOf(Pair(Pair(0f, 22.7f), FatLevel.LOW), Pair(Pair(22.7f, 26.4f), FatLevel.NORMAL), Pair(Pair(26.4f, 30.5f), FatLevel.INCREASED),
                        Pair(Pair(30.5f, 100f), FatLevel.OBESITY))) /* age 30-34*/
                    values.add(linkedMapOf(Pair(Pair(0f, 24f), FatLevel.LOW), Pair(Pair(24f, 27.7f), FatLevel.NORMAL), Pair(Pair(27.7f, 31.5f), FatLevel.INCREASED),
                        Pair(Pair(31.5f, 100f), FatLevel.OBESITY))) /* age 35-39*/
                    values.add(linkedMapOf(Pair(Pair(0f, 25.6f), FatLevel.LOW), Pair(Pair(25.6f, 29.3f), FatLevel.NORMAL), Pair(Pair(29.3f, 32.8f), FatLevel.INCREASED),
                        Pair(Pair(32.8f, 100f), FatLevel.OBESITY))) /* age 40-44*/
                    values.add(linkedMapOf(Pair(Pair(0f, 27.3f), FatLevel.LOW), Pair(Pair(27.3f, 30.9f), FatLevel.NORMAL), Pair(Pair(30.9f, 34.1f), FatLevel.INCREASED),
                        Pair(Pair(34.1f, 100f), FatLevel.OBESITY))) /* age 45-49*/
                    values.add(linkedMapOf(Pair(Pair(0f, 29.7f), FatLevel.LOW), Pair(Pair(29.7f, 33.1f), FatLevel.NORMAL), Pair(Pair(33.1f, 36.2f), FatLevel.INCREASED),
                        Pair(Pair(36.2f, 100f), FatLevel.OBESITY))) /* age 50-54*/
                    values.add(linkedMapOf(Pair(Pair(0f, 30.7f), FatLevel.LOW), Pair(Pair(30.7f, 34f), FatLevel.NORMAL), Pair(Pair(34f, 37.3f), FatLevel.INCREASED),
                        Pair(Pair(37.3f, 100f), FatLevel.OBESITY))) /* age 55-59*/
                    values.add(linkedMapOf(Pair(Pair(0f, 31f), FatLevel.LOW), Pair(Pair(31f, 34.4f), FatLevel.NORMAL), Pair(Pair(34.4f, 38f), FatLevel.INCREASED),
                        Pair(Pair(38f, 100f), FatLevel.OBESITY))) /* age 60 +*/

                }
            }

            var start = 0
            var end = 24
            val age: Int = it?.getAge() ?: 0
            var mapPosition = -1

            for (index in 0..9) {
                if (age in start..end) {
                    mapPosition = index
                    break
                } else mapPosition = -1

                start = end + 1
                if (index < 9) end += 5
                else end = 100
            }

            map(values[mapPosition])
        }
    }

    fun getFatLevel(bf : Float, fatLevel: (FatLevel) -> Unit) {
        getBfInformationMap {map ->
                map.keys.forEach {
                    if (bf > it.first && bf <= it.second) {
                        fatLevel(map.getValue(it))
                    }
                }
        }
    }

    fun getBodyRating(bf : Float, bodyRating: (BodyRating) -> Unit){
        getFatLevel(bf) { fatLevel ->
            getPersonalData {
                when (it?.goal) {
                    Goal.FAT_LOSE -> {
                        when(fatLevel) {
                            FatLevel.LOW -> bodyRating(BodyRating.LOW_BODY_FAT)
                            FatLevel.NORMAL -> bodyRating(BodyRating.MAINTAIN_PROPOSITION)
                            FatLevel.INCREASED -> bodyRating(BodyRating.FAT_LOSE_OK)
                            FatLevel.OBESITY -> bodyRating(BodyRating.FAT_LOSE_OK)
                        }
                    }
                    Goal.MAINTAIN -> {
                        when(fatLevel) {
                            FatLevel.LOW -> bodyRating(BodyRating.BULKING_UP_PROPOSITION)
                            FatLevel.NORMAL -> bodyRating(BodyRating.MAINTAIN_OK)
                            FatLevel.INCREASED -> bodyRating(BodyRating.FAT_LOSE_PROPOSITION)
                            FatLevel.OBESITY -> bodyRating(BodyRating.FAT_LOSE_NEED)
                        }
                    }
                    Goal.MUSCLE_GAIN -> {
                        when(fatLevel) {
                            FatLevel.LOW -> bodyRating(BodyRating.BULKING_UP_OK)
                            FatLevel.NORMAL -> bodyRating(BodyRating.BULKING_UP_OK)
                            FatLevel.INCREASED -> bodyRating(BodyRating.FAT_LOSE_PROPOSITION)
                            FatLevel.OBESITY -> bodyRating(BodyRating.FAT_LOSE_PROPOSITION)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun calculateThumbPosition(fatLevel: FatLevel) : Float {
        var start = 0f
        var end = 0f

        when(fatLevel){
            FatLevel.LOW -> {
                start = 0f
                end = 2f
            }
            FatLevel.NORMAL -> {
                start = 2f
                end = 4f
            }
            FatLevel.INCREASED -> {
                start = 4f
                end = 6f
            }
            FatLevel.OBESITY -> {
                start = 6f
                end = 8f
            }
        }

        val difference = end - start

        val percent = ((difference*100)/end)

        val offset = ((percent*difference)/100)

        val seekValue = start + offset

        return seekValue
    }

}