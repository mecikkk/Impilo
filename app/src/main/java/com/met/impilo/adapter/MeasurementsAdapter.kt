package com.met.impilo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.met.impilo.R
import com.met.impilo.data.BodyMeasurements
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

class MeasurementsAdapter(var bodyMeasurements : BodyMeasurements) : RecyclerView.Adapter<MeasurementsViewHolder>() {

    private val TAG = javaClass.simpleName
    private lateinit var bodyPartNames : List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsViewHolder {
        bodyPartNames = parent.context.resources.getStringArray(R.array.measurements).toList()
        return MeasurementsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.measurement_list_item, parent, false))
    }

    override fun getItemCount(): Int = 9

    override fun onBindViewHolder(holder: MeasurementsViewHolder, position: Int) {
        when(position){
            0 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.waist} cm")
            1 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.bicep} cm")
            2 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.forearm} cm")
            3 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.chest} cm")
            4 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.shoulders} cm")
            5 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.neck} cm")
            6 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.hips} cm")
            7 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.thigh} cm")
            8 -> holder.setData(bodyPartNames[position+1], "${bodyMeasurements.calves} cm")
        }
    }

}

class MeasurementsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    fun setData(name : String, value : String){
        itemView.findViewById<TextView>(R.id.measurement_name).text = name
        itemView.findViewById<TextView>(R.id.measurement_value).text = value
    }
}