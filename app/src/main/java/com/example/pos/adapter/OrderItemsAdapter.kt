package com.example.pos.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.data.entity.Item
import com.example.pos.data.entity.MenuItem
import com.example.pos_admin.R

class OrderItemsAdapter(private val context: Context, private val items: List<MenuItem>, private val listener: OnClickListener )
    :RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>(){
    class OrderItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_img)
        val name: TextView = view.findViewById(R.id.item_name)
        val price: TextView = view.findViewById(R.id.item_price)
        val addToCart: TextView = view.findViewById(R.id.add_to_cart)
        val addMoreLayout: LinearLayout = view.findViewById(R.id.addMoreLayout)
        val imageAdd: ImageView = view.findViewById(R.id.imageAdd)
        val imageMinus: ImageView = view.findViewById(R.id.imageMinus)
        val quantity: TextView = view.findViewById(R.id.quantity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        var currentQuantity = 1
        val item = items[position]
        val id = item.id
        holder.name.text = item.name
        holder.price.text = "Price: $" + item.price
        val decodedString = Base64.decode(item.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.image)
        holder.addToCart.setOnClickListener {
            holder.quantity.text = currentQuantity.toString()
            holder.addToCart.visibility = View.GONE
            holder.addMoreLayout.visibility = View.VISIBLE
            listener.addToMap(item.id, Item(holder.name.text.toString(), holder.quantity.text.toString().toIntOrNull(), item.price, decodedString))

        }
        holder.imageAdd.setOnClickListener {
            holder.quantity.text = (currentQuantity++).toString()
            listener.addToMap(item.id, Item(holder.name.text.toString(), holder.quantity.text.toString().toIntOrNull(),
                item.price, decodedString))
        }
        holder.imageMinus.setOnClickListener {
            currentQuantity--
            if (currentQuantity == 0) {
                holder.addMoreLayout.visibility = View.GONE
                holder.addToCart.visibility = View.VISIBLE
                holder.quantity.text = "0"
                listener.removeFromMap(id)
                currentQuantity == 1
            }
            else {
                holder.quantity.text = currentQuantity.toString()
                listener.addToMap(item.id, Item(holder.name.text.toString(), holder.quantity.text.toString().toIntOrNull(),
                    item.price, decodedString))
            }

        }

    }

    override fun getItemCount() = items.size

    interface OnClickListener {
        fun addToMap(id: Int, item: Item)
        fun removeFromMap(id: Int)
    }
}