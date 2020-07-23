package com.met.impilo.data.workouts.generator

import android.util.Log
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.WhereDoExercise
import com.met.impilo.repository.FirebaseRepository
import com.met.impilo.repository.WorkoutsRepository
import java.math.BigDecimal
import java.math.RoundingMode

class PlanGenerator(var trainingsPerWeek: Int, var trainingInternship: Int, var equipment: WhereDoExercise, var exerciseLoad: ExerciseLoad,
                    var goal: Goal, var bodyMeasurements: BodyMeasurements?) {

    private val TAG = javaClass.simpleName
    lateinit var workoutType: WorkoutType
    private var numberOfReps = mutableListOf<Int>()
    private var numberOfSets = 0
    var trainingPlan: MutableList<TrainingDay> = mutableListOf()

    init{
        setNumberOfSetsAndReps()
        setWorkoutType()
    }

    private fun calculateAllExercises() {
        exerciseLoad.barbellBenchPress = calculate1RM(exerciseLoad.barbellBenchPress)
        exerciseLoad.barbellSquat = calculate1RM(exerciseLoad.barbellSquat)
        exerciseLoad.dumbbellTricepsExtension = calculate1RM(exerciseLoad.dumbbellTricepsExtension)
        exerciseLoad.dumbbellFrontRaise = calculate1RM(exerciseLoad.dumbbellFrontRaise)
        exerciseLoad.dumbbellCurl = calculate1RM(exerciseLoad.dumbbellCurl)
    }

    fun calculate1RM(weight: Float): Float = BigDecimal((((weight * 5) * 0.0333f) + weight).toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toFloat()

    private fun setWorkoutType() {
        workoutType = selectWorkoutType()
    }

    private fun setNumberOfSetsAndReps() {
        numberOfSets = when (goal) {
            Goal.FAT_LOSE -> {
                numberOfReps = mutableListOf(15, 14, 12)
                3
            }
            Goal.MAINTAIN -> {
                numberOfReps = mutableListOf(14, 12, 10)
                3
            }
            Goal.MUSCLE_GAIN -> {
                numberOfReps = mutableListOf(12, 10, 8, 8)
                4
            }
        }
    }

    fun calculateLoads() {
        calculateAllExercises()

        trainingPlan.forEach { day ->
            day.exercises.forEach { exercise ->
                val weight : Float = when (exercise.parent) {
                    1 -> (calculateExerciseLoad(exerciseLoad.barbellBenchPress) * exercise.ratio)
                    2 -> (calculateExerciseLoad(exerciseLoad.barbellSquat) * exercise.ratio)
                    3 -> (calculateExerciseLoad(exerciseLoad.dumbbellTricepsExtension) * exercise.ratio)
                    4 -> (calculateExerciseLoad(exerciseLoad.dumbbellCurl) * exercise.ratio)
                    5 -> (calculateExerciseLoad(exerciseLoad.dumbbellFrontRaise) * exercise.ratio)
                    else -> (calculateExerciseLoad(0f) * exercise.ratio)
                }
                exercise.details.reps = numberOfReps
                exercise.details.sets = numberOfSets
                val bigDecimal = BigDecimal(weight.toDouble()).setScale(1, RoundingMode.HALF_UP).toFloat()
                numberOfReps.forEach { _ ->
                    exercise.details.weight.add(bigDecimal)
                }
            }
        }
    }

    fun calculateExerciseLoad(oneRM: Float): Float {
        return if (goal == Goal.MUSCLE_GAIN) {
            when (trainingInternship) {
                0 -> oneRM * 0.6f
                1 -> oneRM * 0.7f
                2 -> oneRM * 0.8f
                else -> 0f
            }
        } else {
            when (trainingInternship) {
                0 -> oneRM * 0.5f
                1 -> oneRM * 0.6f
                2 -> oneRM * 0.7f
                else -> 0f
            }
        }
    }

    fun selectWorkoutType(): WorkoutType {
        if (goal == Goal.FAT_LOSE) return WorkoutType.THREE_DAY_FAT_LOSS

        when (trainingInternship) {
            0 -> {
                WorkoutType.THREE_DAY_FBW
            }
            1 -> {
                return when {
                    trainingsPerWeek == 4 -> WorkoutType.FOUR_DAY_UP_DOWN
                    trainingsPerWeek <= 3 -> WorkoutType.THREE_DAY_SPLIT
                    else -> WorkoutType.FOUR_DAY_PUSH_PULL
                }
            }

            2 -> {
                return when {
                    trainingsPerWeek < 4 -> WorkoutType.THREE_DAY_SPLIT
                    trainingsPerWeek == 4 -> WorkoutType.FOUR_DAY_PUSH_PULL
                    else -> WorkoutType.FIVE_DAY_SPLIT
                }
            }
        }
        return WorkoutType.THREE_DAY_FBW
    }
}