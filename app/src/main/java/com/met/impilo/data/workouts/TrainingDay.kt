package com.met.impilo.data.workouts

import java.io.Serializable

data class TrainingDay(var id : Int = 0,
                       var muscleSetsNames : MutableList<String> = mutableListOf(),
                       var exercises : MutableList<Exercise> = mutableListOf(),
                       var isRestDay : Boolean = true,
                       var trainingDuration : Float = 0f,
                       var caloriesBurned : Int = 0) : Serializable
