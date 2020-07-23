package com.met.impilo.data.food

import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode


data class FoodNutrients(var energyKcal: Int = 0, var energyKJ: Int = 0, var protein: Float = 0f, var carbo: Float = 0f, var sugars: Float = 0f, var fats: Float = 0f,
                         var fattyAcidsTotalSaturated: Float = 0f, var fattyAcidsTotalTrans: Float = 0f, var fiber: Float = 0f, var salt: Float = 0f) {

    fun calculatePortion(servingSize: Float, portionSize: Float, servingType: ServingType): FoodNutrients {
        val calculated = FoodNutrients()

        val size: Double = if (servingType === ServingType.GRAM) servingSize.toDouble() else (servingSize * portionSize).toDouble()

        calculated.energyKcal = ((size * energyKcal / 100).toInt())
        calculated.energyKJ = ((size * energyKJ / 100).toInt())
        calculated.protein = (BigDecimal(size * this.protein / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.carbo = (BigDecimal(size * carbo / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.sugars = (BigDecimal(size * sugars / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.fats = (BigDecimal(size * fats / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.fattyAcidsTotalSaturated = (BigDecimal(size * fattyAcidsTotalSaturated / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.fattyAcidsTotalTrans = (BigDecimal(size * fattyAcidsTotalTrans / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.fiber = (BigDecimal(size * fiber / 100).setScale(1, RoundingMode.HALF_UP).toFloat())
        calculated.salt = (BigDecimal(size * salt / 100).setScale(1, RoundingMode.HALF_UP).toFloat())

        return calculated
    }
}
