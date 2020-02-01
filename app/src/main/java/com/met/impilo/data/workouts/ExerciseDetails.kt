package com.met.impilo.data.workouts

import java.io.Serializable

data class ExerciseDetails (
    var weight : MutableList<Float> = mutableListOf(),
    var reps : MutableList<Int> = mutableListOf(),
    var time : Float = 0f,
    var distance : Float = 0f,
    var sets : Int = 0
) : Serializable