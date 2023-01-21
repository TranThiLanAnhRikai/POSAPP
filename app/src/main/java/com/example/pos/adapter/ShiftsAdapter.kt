package com.example.pos_admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pos_admin.R
import com.example.pos_admin.data.entity.Shift

class ShiftsAdapter(private val context: Context, private val listOfShifts: List<Shift>):
    RecyclerView.Adapter<ShiftsAdapter.ShiftViewHolder>() {
    class ShiftViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.shift_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.shift, parent, false)

        return ShiftViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        val shift = listOfShifts[position]
        holder.textView.text = shift.shiftName
    }

    override fun getItemCount() = listOfShifts.size

}