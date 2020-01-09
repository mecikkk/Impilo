package com.met.diet.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.baoyz.expandablelistview.BaseSwipeMenuExpandableListAdapter
import com.baoyz.swipemenulistview.ContentViewWrapper
import com.google.android.material.button.MaterialButton
import com.met.diet.R
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealProduct
import kotlin.math.roundToInt


class MealsExpandableListAdapter(private val context : Context, var meals : List<Meal>) : BaseSwipeMenuExpandableListAdapter(){

    private lateinit var callback : OnMealClickListener

    fun setOnMealClickListener(onMealClickListener: OnMealClickListener){
        this.callback = onMealClickListener
    }

    override fun getChildViewAndReUsable(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): ContentViewWrapper {
        var reUseable = true
        val convertView : View

        if (view == null) {
            convertView = View.inflate(context, R.layout.meal_product_item, null)
            reUseable = false
        } else
            convertView = view

        val product = getChild(groupPosition, childPosition)

        val name = convertView.findViewById<TextView>(R.id.product_name_text_view)
        val serving = convertView.findViewById<TextView>(R.id.serving_info_text_view)
        val summary = convertView.findViewById<TextView>(R.id.summary_info_text_view)
        val energy = convertView.findViewById<TextView>(R.id.energy_text_view)

        val c = context.resources.getString(com.met.impilo.R.string.carbohydratesLetter)
        val p = context.resources.getString(com.met.impilo.R.string.proteinsLetter)
        val f = context.resources.getString(com.met.impilo.R.string.fatsLetter)

        name.text = product.name

        summary.text = StringBuilder("$c: ${product.carbohydratesSum.roundToInt()}g  $p: ${product.proteinsSum.roundToInt()}g  $f: ${product.fatsSum.roundToInt()}g")
        energy.text = StringBuilder("${product.kcalSum} kcal")

        if(product.servingName == "g" || product.servingName == "ml"){
            serving.text = StringBuilder("${product.servingSize.roundToInt()}${product.servingName}")
        } else
            serving.text = StringBuilder("${product.servingSize.roundToInt()} x ${product.servingName}")


        name.setOnClickListener {
            callback.onProductClick(groupPosition, childPosition)
        }

        return ContentViewWrapper(convertView, reUseable)
    }

    override fun getGroup(groupPosition: Int): Meal = meals[groupPosition]

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    override fun isGroupSwipable(p0: Int): Boolean = false

    override fun getChildrenCount(groupPosition: Int): Int =
        if(meals[groupPosition].mealProducts.isNullOrEmpty()) 0
        else meals[groupPosition].mealProducts.size


    override fun isChildSwipable(p0: Int, p1: Int): Boolean = true

    override fun getChild(groupPosition: Int, childPosition: Int): MealProduct = meals[groupPosition].mealProducts[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = meals.size


    override fun getGroupViewAndReUsable(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): ContentViewWrapper {
        var reuseable = true
        val convertView : View

        if (view == null) {
            convertView = View.inflate(context, R.layout.meal_item, null)
            reuseable = false
        } else
            convertView = view

        val meal : Meal = getGroup(groupPosition)

        val name = convertView.findViewById<TextView>(R.id.meal_name_text_view)
        val summary = convertView.findViewById<TextView>(R.id.summary_info_text_view)
        val addProductButton = convertView.findViewById<MaterialButton>(R.id.add_product_button)
        val expandBg = convertView.findViewById<LinearLayout>(R.id.some_bg)

        addProductButton.setOnClickListener {
            callback.onAddProductClick(groupPosition, meals[groupPosition].name)
        }

        convertView.setOnClickListener {
            if(!isExpanded)
                expandBg.visibility = View.VISIBLE
            else
                expandBg.visibility = View.INVISIBLE

            callback.onMealClick(groupPosition)
        }

        val e = context.resources.getString(com.met.impilo.R.string.energyLetter)
        val c = context.resources.getString(com.met.impilo.R.string.carbohydratesLetter)
        val p = context.resources.getString(com.met.impilo.R.string.proteinsLetter)
        val f = context.resources.getString(com.met.impilo.R.string.fatsLetter)

        name.text = meal.name
        summary.text = StringBuilder("$e: ${meal.kcalSum} kcal  $c: ${meal.carbohydratesSum.roundToInt()}g  $p: ${meal.proteinsSum.roundToInt()}g  $f: ${meal.fatsSum.roundToInt()}g")

        return ContentViewWrapper(convertView, reuseable)
    }


}

interface OnMealClickListener {
    fun onMealClick(groupPosition: Int)
    fun onAddProductClick(groupPosition: Int, mealName : String)
    fun onProductClick(groupPosition: Int, productPosition : Int)
}