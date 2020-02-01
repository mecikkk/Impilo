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
//        val color = context.resources.getColor(colorId)

        exercise.mainMuscle.forEach { musclesSet ->
            musclesSet.muscles.forEach {
                Log.i("MarkMuscle", "Marking main muscles : $it")
                try {
                    changePathColor(vector, it, context.resources.getColor(R.color.colorAccent))
                }catch (e : IllegalStateException){
                    Log.e("MarkMuscles", "Muscle nod found")
                }
            }
        }

        exercise.supportMuscles?.forEach { musclesSet ->
            musclesSet.muscles.forEach {
                Log.i("MarkMuscle", "Marking main muscles : $it")
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
//        val color = context.resources.getColor(colorId)

        trainingDay.exercises.forEach { exercise ->

            exercise.supportMuscles?.forEach { musclesSet ->
                musclesSet.muscles.forEach {
                    Log.d("MarkMuscles", "Marking support: $it")
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
                    Log.d("MarkMuscles", "Marking main: $it")
                    try {
                        changePathColor(vector, it, context.resources.getColor(R.color.colorAccent))
                    }catch (e : IllegalStateException){
                        Log.e("MarkMuscles", "Muscle nod found")
                    }
                }
            }

        }

    }

    fun changePathColor(vector: VectorChildFinder, pathName: String, color: Int) {
        val path1: VectorDrawableCompat.VFullPath = vector.findPathByName(pathName)
        path1.fillColor = color
    }
}