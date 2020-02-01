package com.met.impilo.repository

import android.util.Log
import com.met.impilo.data.*
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.utils.Const
import com.met.impilo.utils.toId
import java.util.*

class DemandRepository : FirebaseRepository() {
    override val TAG = javaClass.simpleName

    private lateinit var personalData: PersonalData
    private lateinit var bodyMeasurements: BodyMeasurements
    private var demand = Demand()

    companion object {
        @JvmStatic
        fun newInstance() = DemandRepository()
    }

    fun setOrUpdateDemand(personalData: PersonalData, bodyMeasurements: BodyMeasurements, success: (Boolean) -> Unit) {
        Log.i(TAG, "BodyMeasurement : $bodyMeasurements")
        this.bodyMeasurements = bodyMeasurements

        getDemand {
            demand = it
        }

        this.personalData = personalData
        demand.calories = getCaloricDemand()
        calculateMacronutrientsDemand()

        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_DEMAND_COLLECTION).document(Const.REF_DEMAND).set(demand).addOnSuccessListener {
            success(true)
            Log.i(TAG, "Demand added correctly")
        }.addOnFailureListener {
            success(false)
            Log.e(TAG, "Demand adding error : " + it.cause + " | " + it.message)
        }
    }

    fun getDemandInPercentages(date: Date, calculatedDemand: (Demand) -> Unit) {
        var mealsSummary: MealsSummary

        getMealsSummary(date.toId()) { summary ->
            mealsSummary = summary ?: MealsSummary()
            getDemand {
                demand = it
                calculatedDemand(calculateDemandToPercentages(mealsSummary))
            }
        }
    }

    private fun calculateDemandToPercentages(mealsSummary: MealsSummary): Demand {

        Log.i(TAG, "MyDemand : $demand")
        Log.i(TAG, "MealSummary : $mealsSummary")

        val caloriesPercent = ((mealsSummary.kcalSum.toFloat() / demand.calories.toFloat()) * 100).toInt()
        val carboPercent = (mealsSummary.carbohydratesSum / demand.carbohydares) * 100
        val proteinsPercent = (mealsSummary.proteinsSum / demand.proteins) * 100
        val fatsPercent = (mealsSummary.fatsSum / demand.fats) * 100

        Log.i(TAG, "Calculated demand in percent : $caloriesPercent | $carboPercent | $proteinsPercent | $fatsPercent")

        return Demand(caloriesPercent, carboPercent.toInt(), proteinsPercent.toInt(), fatsPercent.toInt())
    }

    private fun getCaloricDemand(): Int {

        val bmr = calculateBmr()
        val tea = calculateTea(bmr)
        val neat = calculateNeat()
        val tef = calculateTfe(bmr, neat.toFloat(), tea)

        val goal = when (personalData.goal) {
            Goal.FAT_LOSE -> -300
            Goal.MAINTAIN -> 0
            Goal.MUSCLE_GAIN -> 200
            else -> 0
        }

        return (bmr + tea + tef + neat + goal).toInt()
    }

    private fun calculateBmr(): Float {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val actualYear = calendar.get(Calendar.YEAR)
        calendar.time = personalData.birthDate!!
        val birthYear = calendar.get(Calendar.YEAR)

        return when (personalData.gender) {
            Gender.MALE -> {
                //Log.e(TAG, "BMR = (9.99 * ${bodyMeasurements.weight}) + (6.25 * ${bodyMeasurements.height}) - (4.92 * ($actualYear - $birthYear)) + 5)")
                ((9.99f * bodyMeasurements.weight) + (6.25f * bodyMeasurements.height) - (4.92f * (actualYear - birthYear)) + 5)
            }
            Gender.FEMALE -> {
                //Log.e(TAG, "BMR = (9.99 * ${bodyMeasurements.weight}) + (6.25 * ${bodyMeasurements.height}) - (4.92 * ($actualYear - $birthYear)) - 161)")
                ((9.99f * bodyMeasurements.weight) + (6.25f * bodyMeasurements.height) - (4.92f * (actualYear - birthYear)) - 161)
            }
            else -> 0f
        }
    }

    private fun calculateTea(bmr: Float): Float {
        val kcalPerMinute = when (personalData.workoutStyle) {
            WorkoutStyle.LIGHT -> 6
            WorkoutStyle.MODERATE -> 8
            WorkoutStyle.HEAVY -> 10
            else -> 0
        }

        val epoc: Float = personalData.workoutQuantity * (0.07 * bmr).toFloat()
        val tea: Float = ((kcalPerMinute * (personalData.workoutTime * personalData.workoutQuantity)) + epoc)

        return tea / 7
    }

    private fun calculateNeat(): Int {
        return when (personalData.somatotype) {
            Somatotype.ECTOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 700
                    Lifestyle.LIGHT -> 750
                    Lifestyle.ACTIVE -> 850
                    Lifestyle.VERY_ACTIVE -> 900
                    else -> 0
                }
            }
            Somatotype.ECTO_MESOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 600
                    Lifestyle.LIGHT -> 650
                    Lifestyle.ACTIVE -> 700
                    Lifestyle.VERY_ACTIVE -> 750
                    else -> 0
                }
            }
            Somatotype.MESO_ECTOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 500
                    Lifestyle.LIGHT -> 550
                    Lifestyle.ACTIVE -> 600
                    Lifestyle.VERY_ACTIVE -> 650
                    else -> 0
                }
            }
            Somatotype.MESOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 400
                    Lifestyle.LIGHT -> 450
                    Lifestyle.ACTIVE -> 450
                    Lifestyle.VERY_ACTIVE -> 500
                    else -> 0
                }
            }
            Somatotype.MESO_ENDOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 300
                    Lifestyle.LIGHT -> 350
                    Lifestyle.ACTIVE -> 400
                    Lifestyle.VERY_ACTIVE -> 450
                    else -> 0
                }
            }
            Somatotype.ENDO_MESOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 250
                    Lifestyle.LIGHT -> 300
                    Lifestyle.ACTIVE -> 350
                    Lifestyle.VERY_ACTIVE -> 450
                    else -> 0
                }
            }
            Somatotype.ENDOMORPH -> {
                when (personalData.lifestyle) {
                    Lifestyle.SEDENTARY -> 200
                    Lifestyle.LIGHT -> 250
                    Lifestyle.ACTIVE -> 300
                    Lifestyle.VERY_ACTIVE -> 400
                    else -> 0
                }
            }
            else -> 0
        }
    }

    private fun calculateTfe(bmr: Float, neat: Float, tea: Float): Float = (0.08 * (bmr + tea + neat)).toFloat()

    private fun calculateMacronutrientsDemand() {
        demand.calories = getCaloricDemand()
        demand.proteins = (2 * bodyMeasurements.weight).toInt()
        demand.fats = bodyMeasurements.weight.toInt()

        val carbohydratesCalories = (demand.calories - ((demand.proteins * 4) + (demand.fats * 9)))
        demand.carbohydares = (carbohydratesCalories / 4)
    }
}