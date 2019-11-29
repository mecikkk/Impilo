package com.met.auth.registration.configuration

import androidx.fragment.app.Fragment
import com.met.impilo.data.*
import java.util.*

open class BaseFragment : Fragment() {
    lateinit var callback : OnDataSendListener

    fun setOnDataSendListener(callback : OnDataSendListener){
        this.callback = callback
    }

    interface OnDataSendListener {
        fun accountCreated()
        fun basicInformation(height : Int, weight : Float, waist: Float, gender: Gender, birthDate : Date? = null)
        fun somatotype(somatotype: Somatotype)
        fun workoutStyle(workoutStyle: WorkoutStyle, trainingsPerWeek : Int, trainingTime : Int)
        fun lifestyle(lifestyle: Lifestyle, goal: Goal)
    }
}