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
import com.example.pos.data.entity.MenuItem
import com.example.pos_admin.R

class OrderItemsAdapter(private val context: Context, private val items: List<MenuItem> )
    :RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>(){
    class OrderItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_img)
        val name: TextView = view.findViewById(R.id.item_name)
        val price: TextView = view.findViewById(R.id.item_price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.price.text = "Price: $" + item.price
        val decodedString = Base64.decode(item.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.image)

    }

    override fun getItemCount() = items.size
}