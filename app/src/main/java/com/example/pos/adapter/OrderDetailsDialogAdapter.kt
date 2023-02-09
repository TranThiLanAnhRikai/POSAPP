package com.example.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.data.entity.CartItem
import com.example.pos_admin.R
import com.example.pos_admin.data.entity.Notification

class OrderDetailsDialogAdapter(private val context: Context, private val orderItems: List<CartItem>)
    : RecyclerView.Adapter<OrderDetailsDialogAdapter.CartItemViewHolder>() {
    class CartItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.order_details_item, parent, false)
        return CartItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        holder.itemName.text = orderItem.itemName
        holder.itemQuantity.text = orderItem.quantity
    }

    override fun getItemCount(): Int = orderItems.size
}