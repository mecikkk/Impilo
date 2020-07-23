package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.utils.Const

class FoodProductsRepository(override val firestore: FirebaseFirestore) : FirebaseRepository(firestore) {

    override val TAG = javaClass.simpleName
    override val uid: String = FirebaseAuth.getInstance().uid!!

    companion object {
        fun newInstance(firestore: FirebaseFirestore) = FoodProductsRepository(firestore)
    }

    fun findProductByName(query: String, result: (List<FoodProduct>) -> Unit) {
        val products = mutableListOf<FoodProduct>()
        firestore.collection(Const.REF_PRODUCTS).whereGreaterThanOrEqualTo("name", query.capitalize()).whereLessThanOrEqualTo("name", "${query.capitalize()}\uf8ff").get()
            .addOnSuccessListener { all ->
                all.forEach {
                    val p = it.toObject(FoodProduct::class.java)
                    p.id = it.id
                    products.add(p)
                }
                result(products)
            }.addOnFailureListener {
                Log.e(TAG, "No data founded")
            }
    }

    fun getProductById(productId: String, product: (FoodProduct) -> Unit) {
        firestore.collection(Const.REF_PRODUCTS).document(productId).get().addOnSuccessListener {
            product(it.toObject(FoodProduct::class.java)!!)
        }.addOnFailureListener {
            Log.e(TAG, "Getting Product error : " + it.cause + " | " + it.message)
        }
    }

    fun getProductByBarcode(barcode: String, product: (FoodProduct?) -> Unit) {
        firestore.collection(Const.REF_PRODUCTS).whereEqualTo("barcode", barcode).get().addOnSuccessListener { all ->

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