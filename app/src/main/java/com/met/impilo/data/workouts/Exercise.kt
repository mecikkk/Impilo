package com.met.impilo.data.workouts

import com.met.impilo.data.MusclesSet
import java.io.Serializable


data class Exercise(var name: String = "", var exerciseType : ExerciseType = ExerciseType.WITH_WEIGHT, var mainMuscle: ArrayList<MusclesSet> = arrayListOf(), var supportMuscles: ArrayList<MusclesSet>? = arrayListOf(),
                    var exercisePlace: ArrayList<WhereDoExercise>? = arrayListOf(), var kcalPerHour: Int = 0, var details: ExerciseDetails = ExerciseDetails()) : Serializable
