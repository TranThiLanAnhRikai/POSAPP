package com.example.pos.adapter

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.data.entity.Item
import com.example.pos_admin.R

class CartItemsAdapter(private val context: Context, private val items: MutableMap<Int, Item>,
                       private val listener: OnClickListener):
RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>(){

    class  CartItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val pricePerItem: TextView = view.findViewById(R.id.price_per_item)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val minusLayout: View = view.findViewById(R.id.minus_layout)
        val plusLayout: View = view.findViewById(R.id.plus_layout)
        val removeLayout: View = view.findViewById(R.id.remove_layout)
        val cartItem: View = view.findViewById(R.id.cart_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val keys = items.keys.toList()
        val values = items.values.toList()
        val item = values[position]
        Log.d(TAG, "item $item")
        val id = keys[position]
        var currentQuantity = values[position].quantity
        holder.name.text = values[position].name
        holder.pricePerItem.text = "$" + values[position].price
        holder.quantity.text = currentQuantity.toString()
        Log.d(TAG, "image ${item.image}")
        val decodedString = Base64.decode(item!!.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.image)
        holder.minusLayout.setOnClickListener{
            currentQuantity = currentQuantity!!.minus(1)
            if (currentQuantity == 0) {
                items.remove(id)
                notifyDataSetChanged()
                listener.removeFromCart(id)
            }
            else {
                holder.quantity.text = currentQuantity.toString()
                listener.decreaseQuantity(id, Item(holder.name.text.toString(), holder.quantity.text.toString().toIntOrNull(), item!!.price, item.image, (item.price.toDouble() * holder.quantity.text.toString().toDouble())))
            }

        }
        holder.removeLayout.setOnClickListener{
            items.remove(id)
            notifyDataSetChanged()
            listener.removeFromCart(id)
        }
        holder.plusLayout.setOnClickListener {
            currentQuantity = currentQuantity!!.plus(1)
            holder.quantity.text = currentQuantity.toString()
            listener.increaseQuantity(id, Item(holder.name.text.toString(), holder.quantity.text.toString().toIntOrNull(), item!!.price, item.image, (item.price.toDouble() * holder.quantity.text.toString().toDouble())))
        }
    }

    override fun getItemCount() = items.size
    interface OnClickListener {
        fun increaseQuantity(id: Int, item: Item)
        fun decreaseQuantity(id: Int, item: Item)
        fun removeFromCart(id: Int)
    }
}