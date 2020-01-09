package com.met.impilo.repository

import android.util.Log
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.utils.Constants

class FoodProductsRepository : FirebaseRepository() {

    override val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = FoodProductsRepository()
    }

    fun findProductByName(query: String, result: (List<FoodProduct>) -> Unit) {
        val products = mutableListOf<FoodProduct>()
        firestore.collection(Constants.REF_PRODUCTS).whereGreaterThanOrEqualTo("name", query.capitalize()).whereLessThanOrEqualTo("name", "${query.capitalize()}\uf8ff").get()
            .addOnSuccessListener { all ->
                all.forEach {
                    val p = it.toObject(FoodProduct::class.java)
                    p.id = it.id
                    products.add(p)
                    //list.add(ProductSearchItem(it.id, it.getString("name")!!, it.getString("producer")!!))
                    Log.e(TAG, "Substring : $query | FOUND : ${it.getString("name")}")
                }
                result(products)
            }.addOnFailureListener {
                Log.e(TAG, "No data founded")
            }
    }

    fun getProductById(productId: String, product: (FoodProduct) -> Unit) {
        firestore.collection(Constants.REF_PRODUCTS).document(productId).get().addOnSuccessListener {
            product(it.toObject(FoodProduct::class.java)!!)
        }.addOnFailureListener {
            Log.e(TAG, "Getting Product error : " + it.cause + " | " + it.message)
        }
    }

    fun getProductByBarcode(barcode: String, product: (FoodProduct?) -> Unit) {
        firestore.collection(Constants.REF_PRODUCTS).whereEqualTo("barcode", barcode).get().addOnSuccessListener { all ->

            if (!all.isEmpty) all.forEach {
                product(it.toObject(FoodProduct::class.java))
            }
            else product(null)

        }.addOnFailureListener {
            Log.e(TAG, "Getting Product error : " + it.cause + " | " + it.message)
            product(null)
        }
    }

}