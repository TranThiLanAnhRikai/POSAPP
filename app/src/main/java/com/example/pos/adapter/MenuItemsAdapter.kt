package com.example.pos.adapter

import android.annotation.SuppressLint
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
import com.example.pos_admin.R
import com.example.pos.data.entity.MenuItem


class MenuItemsAdapter(private val context: Context, private val listOfItems: List<MenuItem>):
    RecyclerView.Adapter<MenuItemsAdapter.MenuItemViewHolder>() {
    class MenuItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val itemImg: ImageView = view.findViewById(R.id.item_img)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemType: TextView = view.findViewById(R.id.item_type)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)

        return MenuItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val item = listOfItems[position]
        holder.itemName.text = item.name
        holder.itemType.text = "Type: " + item.type
        holder.itemPrice.text = "Price: $" + item.price
        val decodedString = Base64.decode(item.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        Glide.with(context)
            .load(decodedByte)
            .into(holder.itemImg)


    }

    override fun getItemCount() = listOfItems.size
}