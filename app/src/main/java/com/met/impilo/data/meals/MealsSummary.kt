package com.met.impilo.data.meals

import java.util.*

data class MealsSummary(
    var date : Date? = null,
    var kcalSum : Int = 0,
    var carbohydratesSum : Float = 0f,
    var proteinsSum : Float = 0f,
    var fatsSum : Float = 0f
)