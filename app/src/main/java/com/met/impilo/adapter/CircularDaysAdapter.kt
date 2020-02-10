package com.met.impilo.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.met.impilo.R
import kotlinx.android.synthetic.main.circular_day_item.view.*


class CircularDaysAdapter(val context: Context) : RecyclerView.Adapter<CircularDaysViewHolder>() {

    private var onDayItemClickListener: DayItemClickListener? = null
    private var clickedItem = 0
    private var previousItem = 0
    private var color : Int = Color.WHITE
    private var isDayNightStyle = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularDaysViewHolder {
        return CircularDaysViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circular_day_item, parent, false), this)
    }

    override fun onBindViewHolder(holder: CircularDaysViewHolder, position: Int) {
        if (position == clickedItem) {
            holder.setToggleText((position + 1).toString(), color, isDayNightStyle, true)
        } else {
            holder.setToggleText((position + 1).toString(), color, isDayNightStyle, false)
        }


    }

    override fun getItemCount(): Int {
        return 7
    }

    fun itemClicked(position: Int) {
        if (onDayItemClickListener != null) onDayItemClickListener!!.onDayItemClick(position)
        previousItem = clickedItem
        clickedItem = position
        if (previousItem != clickedItem) notifyDataSetChanged()
    }

    fun setDayNightStyle(){
        color = ContextCompat.getColor(context, com.met.impilo.R.color.textColor)
        isDayNightStyle = true
        notifyDataSetChanged()
    }

    fun setOnDayItemClickListener(onDayItemClickListener: DayItemClickListener?) {
        this.onDayItemClickListener = onDayItemClickListener
    }
}

class CircularDaysViewHolder(itemView: View, adapter: CircularDaysAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var adapter: CircularDaysAdapter

    fun setToggleText(text: String?, color: Int, isDayNighyStyle : Boolean, active: Boolean) {
        itemView.one_toggle!!.text = text
        if (active) {
            if(isDayNighyStyle)
                itemView.one_toggle!!.background = ContextCompat.getDrawable(adapter.context!!,R.drawable.circle_enabled_day_night)
            else
                itemView.one_toggle!!.background = ContextCompat.getDrawable(adapter.context!!,R.drawable.circle_enabled)

            itemView.one_toggle.setTextColor(ContextCompat.getColor(adapter.context!!,R.color.colorAccent))
        } else {
            if(isDayNighyStyle)
                itemView.one_toggle!!.background = ContextCompat.getDrawable(adapter.context!!,R.drawable.circle_disabled_day_night)
            else
                itemView.one_toggle!!.background = ContextCompat.getDrawable(adapter.context!!,R.drawable.circle_disabled)

            itemView.one_toggle.setTextColor(color)
        }
    }

    override fun onClick(view: View?) {
        adapter.itemClicked(adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
        this.adapter = adapter
    }
}

interface DayItemClickListener {
    fun onDayItemClick(position: Int)
}