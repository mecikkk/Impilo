package com.met.diet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.met.diet.R
import com.met.impilo.data.food.FoodProduct


class SearchActivityAdapter(val history  : List<FoodProduct>, private val context: Context) : BaseAdapter() {

    var productsNamesFiltered : List<FoodProduct> = history
    lateinit var callback : OnProductClickListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(context).inflate(R.layout.search_product_item, null)

        val historyIc = v.findViewById<ImageView>(R.id.history_icon)
        val productName = v.findViewById<TextView>(R.id.product_name_text_view)
        val producerName = v.findViewById<TextView>(R.id.producer_name_text_view)

        productName.text = productsNamesFiltered[position].name
        producerName.text = productsNamesFiltered[position].producer
        historyIc.visibility = View.INVISIBLE

        v.setOnClickListener {
            callback.onProductClick(productsNamesFiltered[position])
        }

        return v
    }

    fun setOnProductClickListener(onProductClickListener: OnProductClickListener){
        this.callback = onProductClickListener
    }

    override fun getItem(position: Int): Any = productsNamesFiltered[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = productsNamesFiltered.size


    interface OnProductClickListener {
        fun onProductClick(foodProduct: FoodProduct)
    }
}