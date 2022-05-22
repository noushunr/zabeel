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
import com.greenflames.myzebeel.preferences.MESSAGE_LOWER_BOUND
import com.greenflames.myzebeel.preferences.MESSAGE_SOMETHING_WRONG
import com.greenflames.myzebeel.preferences.MESSAGE_UPPER_BOUND
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 06-05-2021
*/

class ReorderCartListAdapter(private val mListener: OnCartListClickListener)
    : RecyclerView.Adapter<ReorderCartListAdapter.ViewHolder>() {

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
        holder.bind(differ.currentList[position], mListener)
    }

    override fun getItemCount() = differ.currentList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.product_name_textView)

        private val decrementTextView: TextView = itemView.findViewById(R.id.decrement2)
        private val incrementTextView:TextView = itemView.findViewById(R.id.increment2)
        private val qtyTextView: TextView = itemView.findViewById(R.id.display_inc_dec2)

        fun bind(item: OrderDetailItem, callBack: OnCartListClickListener) {
            nameTextView.text = item.name
            val qty = item.qty_ordered.replace(".0", "")
            qtyTextView.text = qty
            incrementTextView.setOnClickListener {
                try {
                    val intQty = qty.toInt()
                    if (intQty < 21) {
                        callBack.onIncDecClick(adapterPosition, item, "${intQty+1}")
                    } else {
                        throwError(item, callBack, MESSAGE_UPPER_BOUND, adapterPosition)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throwError(item, callBack, MESSAGE_SOMETHING_WRONG, adapterPosition)
                }
            }

            decrementTextView.setOnClickListener {
                try {
                    val intQty = qty.toInt()
                    if (intQty != 0 && intQty != 1) {
                        callBack.onIncDecClick(adapterPosition, item, "${intQty-1}")
                    } else {
                        throwError(item, callBack, MESSAGE_LOWER_BOUND, adapterPosition)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throwError(item, callBack, MESSAGE_SOMETHING_WRONG, adapterPosition)
                }
            }


        }

        private fun throwError(item: OrderDetailItem, callBack: OnCartListClickListener, message: String, position: Int) {
            callBack.onErrorClick(adapterPosition, item, message)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.reorder_cart_card, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface OnCartListClickListener {
        fun onCartClick(position: Int, item: OrderDetailItem)
        fun onIncDecClick(position: Int, item: OrderDetailItem, qty: String)
        fun onErrorClick(position: Int, item: OrderDetailItem, message: String)
    }

}