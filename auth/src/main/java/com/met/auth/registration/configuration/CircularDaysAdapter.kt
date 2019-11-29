package com.met.auth.registration.configuration

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.auth.R
import kotlinx.android.synthetic.main.circular_day_item.view.*


class CircularDaysAdapter : RecyclerView.Adapter<CircularDaysViewHolder>() {

    private var onDayItemClickListener: DayItemClickListener? = null
    private var clickedItem = 0
    private var previousItem = 0
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularDaysViewHolder {
        context = parent.context
        return CircularDaysViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circular_day_item, parent, false), this)
    }

    override fun onBindViewHolder(holder: CircularDaysViewHolder, position: Int) {
        if (position == clickedItem) {
            holder.setToggleText((position + 1).toString(), true)
        } else {
            holder.setToggleText((position + 1).toString(), false)
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

    fun setOnDayItemClickListener(onDayItemClickListener: DayItemClickListener?) {
        this.onDayItemClickListener = onDayItemClickListener
    }
}

class CircularDaysViewHolder(itemView: View, adapter: CircularDaysAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var adapter: CircularDaysAdapter
    fun setToggleText(text: String?, active: Boolean) {
        itemView.one_toggle!!.text = text
        if (active) {
            itemView.one_toggle!!.background = adapter.context?.resources?.getDrawable(R.drawable.circle_enabled)
            itemView.one_toggle.setTextColor(adapter.context?.resources!!.getColor(com.met.impilo.R.color.colorAccent))
        } else {
            itemView.one_toggle!!.background = adapter.context?.resources?.getDrawable(R.drawable.circle_disabled)
            itemView.one_toggle.setTextColor(Color.WHITE)
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