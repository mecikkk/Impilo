package com.met.impilo.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager

object ScreenUtils {

    fun getScreenHeight(context: Context): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels.toFloat()
    }

    fun dpToPx(value : Int, context: Context) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), context.resources.displayMetrics)
}
