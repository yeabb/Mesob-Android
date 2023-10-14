package com.example.mesob

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Typeface


class TimeSlotAdapter(private val timeWindows: List<TimeWindow>) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_timewindow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeWindow = timeWindows[position]
        holder.bind(timeWindow)

        if (position == selectedPosition) {
            // Change the appearance of the selected item here.
            holder.tvTimeWindow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange))
            holder.tvTimeWindow.setTypeface(holder.defaultTextStyle, Typeface.BOLD)
        } else {
            // Restore the default appearance for non-selected items.
            holder.tvTimeWindow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.tvTimeWindow.setTypeface(holder.defaultTextStyle, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Notify the adapter to refresh the views of the previously selected and newly selected items.
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = timeWindows.size



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTimeWindow: TextView = itemView.findViewById(R.id.tvTimeWindow)
        val defaultTextStyle = tvTimeWindow.typeface

        fun bind(timeWindow: TimeWindow) {
            val timeWindowToDisplay = "${timeWindow.startTime} - ${timeWindow.endTime}"
            val tvTimeWindow = itemView.findViewById<TextView>(R.id.tvTimeWindow)

            tvTimeWindow.text = timeWindowToDisplay

        }
    }

    fun getSelectedTimeWindow(): TimeWindow? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            timeWindows[selectedPosition]
        } else {
            null
        }
    }
}
