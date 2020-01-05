package com.met.diet.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.met.diet.R
import com.met.impilo.data.food.FoodNutrients


class ProductDetailsAdapter internal constructor(private val context: Context, product100gNutrients: FoodNutrients,
                                                 productPortionNutrients: FoodNutrients) : RecyclerView.Adapter<ProductDetailViewHolder>() {
    private val TAG = javaClass.simpleName

    var nutrientName: ArrayList<String>
    var nutrient100gValue: ArrayList<Number>
    var nutrientPortionValue: ArrayList<Number>

    private fun getListOfNutrients(nutrients: FoodNutrients): ArrayList<Number> {
        val nutrientValues: ArrayList<Number> = ArrayList()
        nutrientValues.add(nutrients.energyKcal)
        nutrientValues.add(nutrients.energyKJ)
        nutrientValues.add(nutrients.carbo)
        nutrientValues.add(nutrients.sugars)
        nutrientValues.add(nutrients.protein)
        nutrientValues.add(nutrients.fats)
        nutrientValues.add(nutrients.fattyAcidsTotalSaturated)
        nutrientValues.add(nutrients.fattyAcidsTotalTrans)
        nutrientValues.add(nutrients.fiber)
        nutrientValues.add(nutrients.salt)
        return nutrientValues
    }

    fun updateData(productPortionNutrients: FoodNutrients) {
        nutrientPortionValue = getListOfNutrients(productPortionNutrients)
        Log.d(TAG, productPortionNutrients.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        return ProductDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.nutrients_item, parent, false))
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        when (position) {
            0 -> holder.bind(nutrientName[position], nutrient100gValue[position], nutrientPortionValue[position], "kcal")
            1 -> holder.bind(nutrientName[position], nutrient100gValue[position], nutrientPortionValue[position], "kJ")
            else -> holder.bind(nutrientName[position], nutrient100gValue[position], nutrientPortionValue[position], "g")
        }
    }

    override fun getItemCount(): Int = nutrientName.size


    init {
        nutrient100gValue = getListOfNutrients(product100gNutrients)
        nutrientPortionValue = getListOfNutrients(productPortionNutrients)
        nutrientName = ArrayList()
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.energyKcal))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.energyKJ))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.carbohydrates))
        nutrientName.add("\t" + context.resources.getString(com.met.impilo.R.string.sugars))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.proteins))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.fats))
        nutrientName.add("\t" + context.resources.getString(com.met.impilo.R.string.saturated_fatty_acids))
        nutrientName.add("\t" + context.resources.getString(com.met.impilo.R.string.unsaturated_fatty_acids))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.fiber))
        nutrientName.add(context.resources.getString(com.met.impilo.R.string.salt))
    }
}

class ProductDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var name: TextView? = itemView.findViewById(R.id.nutrient_name)
    private var value100g: TextView? = itemView.findViewById(R.id.portion_100g)
    private var valueCalculated: TextView? = itemView.findViewById(R.id.portion_calculated_size)

    fun bind(name: String?, value100g: Number, valuePortion: Number, unit: String?) {
        if (adapterPosition == 3 || adapterPosition == 6 || adapterPosition == 7) {
            this.name?.setTextColor(Color.GRAY)
        } else this.name?.setTextColor(ContextCompat.getColor(itemView.context, com.met.impilo.R.color.textColor))
        this.name!!.text = name
        this.value100g!!.text = StringBuilder().append(value100g.toString()).append(" ").append(unit).toString()
        this.valueCalculated!!.text = StringBuilder().append(valuePortion.toString()).append(" ").append(unit).toString()
    }

}