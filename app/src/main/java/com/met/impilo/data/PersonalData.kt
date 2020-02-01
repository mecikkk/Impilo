package com.met.impilo.data

import com.met.impilo.utils.getIntYear
import java.util.*

data class PersonalData(
    var uid: String = "",
    var birthDate: Date? = null,
    var fullName: String? = "",
    var gender: Gender? = null,
    var somatotype: Somatotype? = null,
    var workoutStyle: WorkoutStyle? = null,
    var workoutTime: Int = 0,
    var workoutQuantity: Int = 0,
    var lifestyle: Lifestyle? = null,
    var goal: Goal? = null,
    var baseConfigurationCompleted: Boolean = false,
    val registrationDate: Date? = null
) {

    fun getAge() : Int = (Date().getIntYear().minus(birthDate?.getIntYear()!!))

}