package com.example.pos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.const.Status
import com.example.pos_admin.R
import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.ContentValues.TAG
import android.view.View
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import com.example.pos_admin.data.entity.Order


class OrdersAdapter(
    private val context: Context,
    private val orders: List<Order>,
    private val setListener: SetOnClickListener
) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    class OrderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.order_id)
        val orderNumber: TextView = view.findViewById(R.id.order_number)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val total: TextView = view.findViewById(R.id.total)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.order, parent, false)
        return OrderViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = order.id.toString()
        holder.orderNumber.text = order.orderNumber.toString()
        holder.quantity.text = order.quantity.toString()
        holder.total.text = order.total.toString()
        holder.status.text = order.status
        holder.status.setOnClickListener {
            if (holder.status.text == Status.PROCESSING.name) {
                val statusOptions = arrayOf(Status.DELIVERED.name, Status.CANCELLED.name)
                val builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("Change Status To").setSingleChoiceItems(
                    statusOptions, -1, DialogInterface.OnClickListener { _, which ->
                        val selectedStatus = statusOptions[which]
                        holder.status.text = selectedStatus
                        setListener.updateOrder(
                            Order(
                                order.id,
                                order.orderNumber,
                                order.foodRevenue,
                                order.drinkRevenue,
                                order.dessertRevenue,
                                order.quantity,
                                order.total,
                                selectedStatus,
                                order.deliveryMethod,
                                order.paymentMethod,
                                order.request
                            )
                        )
                    })
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
            } else {
                holder.status.setOnClickListener(null)


            }
        }
    }


    override fun getItemCount(): Int = orders.size

    interface SetOnClickListener {
        fun updateOrder(order: Order)
    }
}