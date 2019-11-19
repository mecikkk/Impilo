package com.met.impilo.utils

import android.content.Context
import android.util.DisplayMetrics
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
}
