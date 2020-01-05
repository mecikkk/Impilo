package com.met.impilo.data.food

data class FoodProduct(
    var id : String = "",
    var name : String = "",
    var producer : String = "",
    var category : FoodCategory? = null,
    var servingName : String = "",
    var servingSize : Float = 0f,
    var barcode : String = "",
    var photoUri : String = "",
    var nutrients : FoodNutrients? = null
) {
    fun getCaluclatedNutrients(servingSize: Float, servingType: ServingType) = nutrients?.calculatePortion(servingSize, this.servingSize, servingType)

    fun updateData(foodProduct: FoodProduct){
        this.name = foodProduct.name
        this.producer = foodProduct.producer
        this.category = foodProduct.category
        this.servingName = foodProduct.servingName
        this.servingSize = foodProduct.servingSize
        this.barcode = foodProduct.barcode
        this.photoUri = foodProduct.photoUri
        this.nutrients = foodProduct.nutrients
    }
}