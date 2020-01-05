package com.met.impilo.data.meals

data class MealProduct(
    var productId : String = "",
    var producer : String = "",
    var name : String = "",
    var kcalSum : Int = 0,
    var carbohydratesSum : Float = 0f,
    var proteinsSum : Float = 0f,
    var fatsSum : Float = 0f,
    var servingName : String = "",
    var servingSize : Float = 0f
)