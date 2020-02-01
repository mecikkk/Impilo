package com.met.impilo.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.Nullable
import androidx.viewpager.widget.ViewPager

class NoSwipeableViewPager(context: Context, @Nullable attrs: AttributeSet?) :
    ViewPager(context, attrs) {
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}