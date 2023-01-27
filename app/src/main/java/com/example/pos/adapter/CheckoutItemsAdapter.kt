package com.example.pos.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.data.entity.Item
import com.example.pos_admin.R

class CheckoutItemsAdapter(private val context: Context, private val items: MutableMap<Int, Item>):
RecyclerView.Adapter<CheckoutItemsAdapter.CheckoutItemViewHolder>(){
    class CheckoutItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_img)
        val quantity: TextView = view.findViewById(R.id.quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.checkout_item, parent, false)
        return CheckoutItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CheckoutItemViewHolder, position: Int) {
        val values = items.values.toList()
        val item = values[position]
        val decodedString = Base64.decode(item!!.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.image)
        holder.quantity.text = item.quantity.toString()
    }

    override fun getItemCount(): Int = items.size

}