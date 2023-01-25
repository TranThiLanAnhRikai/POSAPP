package com.example.pos.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.data.entity.Item
import com.example.pos.data.entity.MenuItem
import com.example.pos.model.MenuViewModel
import com.example.pos_admin.R
import com.example.pos_admin.data.repository.MenuItemRepository

class CartItemsAdapter(private val context: Context, private val items: MutableMap<Int, Item>):
RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>(){

    class  CartItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val pricePerItem: TextView = view.findViewById(R.id.price_per_item)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val subTotal: TextView = view.findViewById(R.id.sub_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val keys = items.keys.toList()
        val values = items.values.toList()
        val id = keys[position]
        Log.d(TAG, "price ${values[position].price.toString()}")
        Log.d(TAG, "quantity ${values[position].quantity.toString()}")
        holder.name.text = values[position].name
        holder.pricePerItem.text = values[position].price.toString()
        holder.quantity.text = values[position].quantity.toString()
        holder.subTotal.text = (values[position].price.toDouble()!! * values[position].quantity!!).toString()
     /*   val decodedString = Base64.decode(values[position].image, Base64.DEFAULT)*/
        val decodedByte = BitmapFactory.decodeByteArray(values[position].image, 0, values[position].image.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.image)
    }

    override fun getItemCount() = items.size

}