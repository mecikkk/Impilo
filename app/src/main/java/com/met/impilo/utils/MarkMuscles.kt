package com.met.impilo.utils

import android.content.Context
import android.widget.ImageView
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import com.met.impilo.R
import com.met.impilo.data.MusclesSet

object MarkMuscles {

    fun set(context : Context, vectorRes : Int, imageView : ImageView, set : MusclesSet){

        val vector = VectorChildFinder(context, vectorRes, imageView)
        val color = context.resources.getColor(R.color.colorAccent)

        set.muscles.forEach {
            changePathColor(vector, it, color)
        }

    }

    fun changePathColor(vector: VectorChildFinder, pathName: String, color: Int) {
        val path1: VectorDrawableCompat.VFullPath = vector.findPathByName(pathName)
        path1.fillColor = color
    }
}