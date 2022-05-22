package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.models.order.OrderDetailItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 09-05-2021
*/

class OrderDetailItemAdapter()
    : RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<OrderDetailItem>() {
        override fun areItemsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
            return oldItem.item_id == newItem.item_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<OrderDetailItem>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                differ.submitList(list.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.list_order_detail_name_txt)
        private val priceTextView: TextView = itemView.findViewById(R.id.list_order_detail_total_txt)
        private val qtyTextView: TextView = itemView.findViewById(R.id.list_order_detail_qty_txt)

        @SuppressLint("SetTextI18n")
        fun bind(item: OrderDetailItem) {
            nameTextView.text = item.name
            val price = item.price.toFloat()
            priceTextView.text = "KD $price"// + String.format("%.3f", price)
            qtyTextView.text = "Qty:${item.qty_ordered}"
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.list_order_details, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}