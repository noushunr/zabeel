package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.helpers.Global.convertDate
import com.greenflames.myzebeel.models.order.OrderItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 08-05-2021
*/

class OrderAdapter(private val mListener: OnOrderListClickListener)
    : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.entity_id == newItem.entity_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<OrderItem>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                differ.submitList(list.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position], mListener)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val orderIdTextView: TextView = itemView.findViewById(R.id.list_order_id_txt)
        private val priceTextView: TextView = itemView.findViewById(R.id.list_order_total_txt)
        private val dateTextView: TextView = itemView.findViewById(R.id.list_order_date_txt)
        private val statusTextView: TextView = itemView.findViewById(R.id.list_order_status_txt)
        private val addressTextView: TextView = itemView.findViewById(R.id.list_order_address_txt)
        private val reOrderBtn = itemView.findViewById<Button>(R.id.orderagain_button)

        @SuppressLint("SetTextI18n")
        fun bind(item1: OrderItem, callBack: OnOrderListClickListener) {
            orderIdTextView.text = "Order #${item1.increment_id}"
            val price = item1.grand_total.toFloat()
            priceTextView.text = "Total : KD $price"// + String.format("%.3f", price)
            dateTextView.text = convertDate(item1.created_at)
            statusTextView.text = "Status :  ${item1.status}"
            if (item1.status.equals("canceled")) {
                statusTextView.setTextColor(Color.RED)
            } else {
                statusTextView.setTextColor(Color.BLACK)
            }

            itemView.rootView.setOnClickListener {
                callBack.onOrderClick(adapterPosition, item1)
            }
            reOrderBtn.setOnClickListener {
                callBack.onReOrderClick(adapterPosition, item1)
            }
            val item = item1.billing_address

            if (item != null) {
                val mAddress = StringBuilder()
                mAddress.append(item.firstname).append(" ").append(item.lastname)
                for (streetName in item.street) {
                    mAddress.append(", ").append(streetName)
                }
                if (item.city != null) {
                    mAddress.append(", ").append(item.city)
                }
                if (item.postcode != null) {
                    mAddress.append(", ").append(item.postcode)
                }
                addressTextView.text = mAddress
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.list_orders, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface OnOrderListClickListener {
        fun onOrderClick(position: Int, item: OrderItem)
        fun onReOrderClick(position: Int, item: OrderItem)
    }
}