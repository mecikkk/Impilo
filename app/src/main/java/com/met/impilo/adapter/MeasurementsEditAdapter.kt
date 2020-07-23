package com.met.impilo.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hadiidbouk.charts.BarData
import com.met.impilo.R
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.utils.getIntDay
import com.met.impilo.utils.getIntMonth
import kotlinx.android.synthetic.main.measurement_edit_list_item.view.*
import kotlinx.android.synthetic.main.measurement_edit_list_item.view.chart_measurement_progress
import kotlinx.android.synthetic.main.measurement_list_item.view.*
import net.cachapa.expandablelayout.util.FastOutSlowInInterpolator
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class MeasurementsEditAdapter(val context : Context, var separatedMeasurements : List<Pair<Date, List<Float>>>, var lastMeasurements : BodyMeasurements) : RecyclerView.Adapter<MeasurementsEditViewHolder>() {

    private val TAG = javaClass.simpleName
    private var bodyPartNames = listOf<String>()
    var newMeasurements = BodyMeasurements()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsEditViewHolder {
        bodyPartNames = parent.context.resources.getStringArray(R.array.measurements).toList()
        separatedMeasurements.sortedBy { it.first }
        newMeasurements.apply {
            uid = lastMeasurements.uid
            height = lastMeasurements.height
            weight = lastMeasurements.weight
            waist = lastMeasurements.waist
            bicep = lastMeasurements.bicep
            forearm = lastMeasurements.forearm
            chest = lastMeasurements.chest
            shoulders = lastMeasurements.shoulders
            neck = lastMeasurements.neck
            hips = lastMeasurements.hips
            thigh = lastMeasurements.thigh
            calves = lastMeasurements.calves
        }
        return MeasurementsEditViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.measurement_edit_list_item, parent, false))
    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: MeasurementsEditViewHolder, position: Int) {
        holder.setBodyPartName(bodyPartNames[position])
        updateMeasurementProgress(position, holder)

        holder.setEditTextTextWatcher(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty()){
                    editValue(s.toString().toFloat(), position)
                } else
                    copyLastMeasurementValues(position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    fun copyLastMeasurementValues(position: Int){
        when(position){
            0 -> newMeasurements.weight = lastMeasurements.weight
            1 -> newMeasurements.waist = lastMeasurements.waist
            2 -> newMeasurements.bicep = lastMeasurements.bicep
            3 -> newMeasurements.forearm = lastMeasurements.forearm
            4 -> newMeasurements.chest = lastMeasurements.chest
            5 -> newMeasurements.shoulders = lastMeasurements.shoulders
            6 -> newMeasurements.neck = lastMeasurements.neck
            7 -> newMeasurements.hips = lastMeasurements.hips
            8 -> newMeasurements.thigh = lastMeasurements.thigh
            9 -> newMeasurements.calves = lastMeasurements.calves
        }
    }

    fun editValue(value: Float, position: Int){
        when(position){
            0 -> newMeasurements.weight = value
            1 -> newMeasurements.waist = value
            2 -> newMeasurements.bicep = value
            3 -> newMeasurements.forearm = value
            4 -> newMeasurements.chest = value
            5 -> newMeasurements.shoulders = value
            6 -> newMeasurements.neck = value
            7 -> newMeasurements.hips = value
            8 -> newMeasurements.thigh = value
            9 -> newMeasurements.calves = value
        }
    }

    private fun updateMeasurementProgress(position: Int, holder : MeasurementsEditViewHolder) {
        val data = ArrayList<BarData>()
        val months = DateFormatSymbols.getInstance().shortMonths

        var allValuesOfConcreteMeasurement = mutableListOf<Float>()

        separatedMeasurements = separatedMeasurements.sortedBy { it.first }

        separatedMeasurements.forEach {
            allValuesOfConcreteMeasurement.add(it.second[position])
        }

        val max = getMaxValue(allValuesOfConcreteMeasurement)
        val min = getMinValue(allValuesOfConcreteMeasurement)

        holder.setChartMaxValue(max - min + 0.2f)

        separatedMeasurements.forEach {
            val chartValue = it.second[position] - min + 0.1f
            if(holder.adapterPosition == 0)
                data.add(BarData("${it.first.getIntDay()} ${months[it.first.getIntMonth() - 1].toUpperCase()}", chartValue, "${it.second[position]} kg"))
            else
                data.add(BarData("${it.first.getIntDay()} ${months[it.first.getIntMonth() - 1].toUpperCase()}", chartValue, "${it.second[position]} cm"))
        }

        if(holder.adapterPosition == 0) {
            holder.setActualValue(StringBuilder("${separatedMeasurements[separatedMeasurements.size - 1].second[position]} kg").toString())
            holder.setEditTextHint(context.getString(R.string.new_measurement) + " (kg)")
        } else {
            holder.setActualValue(StringBuilder("${separatedMeasurements[separatedMeasurements.size - 1].second[position]} cm").toString())
            holder.setEditTextHint(context.getString(R.string.new_measurement) + " (cm)")

        }


        holder.setChartProgressData(data)

    }

    private fun getMaxValue(measurements : List<Float>) : Float{
        var result = measurements[0]

        measurements.forEach {
            if (result < it) result = it
        }
        return result
    }

    private fun getMinValue(measurements : List<Float>) : Float{
        var result = measurements[0]

        measurements.forEach {
            if (result > it) result = it
        }
        return result
    }

}

class MeasurementsEditViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{

    init {
        itemView.expandable_layout.setInterpolator(FastOutSlowInInterpolator())
        itemView.chart_button.setOnClickListener(this)
    }

    fun setBodyPartName(name : String) {
        itemView.measurement_edit_name.text = name
    }

    fun setEditTextHint(hint : String){
        itemView.measured_value_input_text_layout.hint = hint
    }

    fun setActualValue(value : String){
        itemView.measurement_edit_current_value.text = value
    }

    fun setChartMaxValue(max: Float){
        itemView.chart_measurement_progress.setMaxValue(max)
    }

    fun setChartProgressData(data : ArrayList<BarData>){
        itemView.chart_measurement_progress.setDataList(data)
        itemView.chart_measurement_progress.build()
    }

    fun setEditTextTextWatcher(textWatcher: TextWatcher) {
        itemView.measured_value_input_edit_text.addTextChangedListener(textWatcher)
    }


    override fun onClick(v: View?) {
        if(itemView.expandable_layout.isExpanded)
            itemView.expandable_layout.collapse()
        else
            itemView.expandable_layout.expand()

    }


}