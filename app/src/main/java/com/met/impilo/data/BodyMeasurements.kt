package com.met.impilo.data

import java.util.*

data class BodyMeasurements(
    var uid: String = "",
    var height : Int = 0,
    var weight : Float = 0f,
    var waist : Float = 0f,
    var hips : Float = 0f,
    var chest : Float = 0f,
    var thigh : Float = 0f,
    var calves : Float = 0f,
    var bicep : Float = 0f,
    var forearm : Float = 0f,
    var shoulders : Float = 0f,
    var neck : Float = 0f,
    var measurementDate: Date = Date()
)