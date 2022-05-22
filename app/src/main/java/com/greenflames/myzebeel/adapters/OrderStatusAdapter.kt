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
import com.greenflames.myzebeel.models.order.StatusHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 09-05-2021
*/

class OrderStatusAdapter()
    : RecyclerView.Adapter<OrderStatusAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<StatusHistory>() {
        override fun areItemsTheSame(oldItem: StatusHistory, newItem: StatusHistory): Boolean {
            return oldItem.entity_id == newItem.entity_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: StatusHistory, newItem: StatusHistory): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<StatusHistory>) {
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

        private val statusTextView: TextView = itemView.findViewById(R.id.order_status_name_txt)

        fun bind(item: StatusHistory) {
            statusTextView.text = item.status
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.list_order_status, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}