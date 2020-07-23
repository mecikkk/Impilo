@file:Suppress("DEPRECATION")

package com.met.impilo.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import com.met.impilo.R
import com.met.impilo.data.MusclesSet
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.data.workouts.TrainingDay
import java.lang.IllegalStateException

object MarkMuscles {

    fun set(context : Context, vectorRes : Int, imageView : ImageView, exercise : Exercise){

        val vector = VectorChildFinder(context, vectorRes, imageView)

        exercise.mainMuscle.forEach { musclesSet ->
            musclesSet.muscles.forEach {
                try {
                    changePathColor(vector, it, context.resources.getColor(R.color.colorAccent))
                }catch (e : IllegalStateException){
                    Log.e("MarkMuscles", "Muscle nod found")
                }
            }
        }

        exercise.supportMuscles?.forEach { musclesSet ->
            musclesSet.muscles.forEach {
                try {
                    changePathColor(vector, it, context.resources.getColor(R.color.supportMusclesColor))
                } catch (e : IllegalStateException){
                    Log.e("MarkMuscles", "Muscle nod found")
                }
            }
        }
    }

    fun setByTrainingDay(context : Context, vectorRes : Int, imageView : ImageView, trainingDay: TrainingDay){

        val vector = VectorChildFinder(context, vectorRes, imageView)

        trainingDay.exercises.forEach { exercise ->

            exercise.supportMuscles?.forEach { musclesSet ->
                musclesSet.muscles.forEach {
                    try {
                        changePathColor(vector, it, context.resources.getColor(R.color.supportMusclesColor))
                    } catch (e : IllegalStateException){
                        Log.e("MarkMuscles", "Muscle nod found")
                    }
                }
            }
        }

        trainingDay.exercises.forEach { exercise ->

            exercise.mainMuscle.forEach { musclesSet ->
                musclesSet.muscles.forEach {
                    try {
                        changePathColor(vector, it, context.resources.getColor(R.color.colorAccent))
                    }catch (e : IllegalStateException){
                        Log.e("MarkMuscles", "Muscle nod found")
                    }
                }
            }

        }

    }

    fun setOnlyMainMusclesByTrainingDay(context : Context, vectorRes : Int, imageView : ImageView, trainingDay: TrainingDay){

        val vector = VectorChildFinder(context, vectorRes, imageView)

        trainingDay.exercises.forEach { exercise ->

            exercise.mainMuscle.forEach { musclesSet ->
                musclesSet.muscles.forEach {
                    try {
                        changePathColor(vector, it, context.resources.getColor(R.color.colorAccent))
                    }catch (e : IllegalStateException){
                        Log.e("MarkMuscles", "Muscle nod found")
                    }
                }
            }

        }

    }

    private fun changePathColor(vector: VectorChildFinder, pathName: String, color: Int) {
        val path1: VectorDrawableCompat.VFullPath = vector.findPathByName(pathName)
        path1.fillColor = color
    }
}