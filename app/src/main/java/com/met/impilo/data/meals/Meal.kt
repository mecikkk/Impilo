package com.met.impilo.data.meals

data class Meal (
    var id : Int = 0,
    var name : String = "",
    var kcalSum : Int = 0,
    var carbohydratesSum : Float = 0f,
    var proteinsSum : Float = 0f,
    var fatsSum : Float = 0f,
    var mealProducts : MutableList<MealProduct> = mutableListOf()
)