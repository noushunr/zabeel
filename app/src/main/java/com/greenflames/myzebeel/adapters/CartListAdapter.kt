package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.models.cart.CartItem
import com.greenflames.myzebeel.network.Apis
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

class CartListAdapter(private val mListener: OnCartListClickListener)
    : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.item_id == newItem.item_id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<CartItem>) {
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
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price_textView)
        private val qtyTextView: TextView = itemView.findViewById(R.id.display_inc_dec2)
        private val configTextView: TextView = itemView.findViewById(R.id.product_config_textView)
        private val decrementTextView: TextView = itemView.findViewById(R.id.decrement2)
        private val incrementTextView:TextView = itemView.findViewById(R.id.increment2)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageView_cart_product)

        fun bind(item: CartItem, callBack: OnCartListClickListener) {
            nameTextView.text = item.name

            priceTextView.text = "KD ${item.price}"

            val qty = item.qty.replace(".0", "")
            qtyTextView.text = qty
            configTextView.text =   "Qty : $qty"
//            if (item.options != null) {
//                configTextView.text = item.options
//            }

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

            val filePath = item.extension_attributes?.image
            val imgUrl = Apis.PRODUCT_IMG_BASE_URL + filePath
            Glide.with(productImageView)
                    .load(imgUrl)
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(productImageView)
        }

        private fun throwError(item: CartItem, callBack: OnCartListClickListener, message: String, position: Int) {
            callBack.onErrorClick(adapterPosition, item, message)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.cart_card, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface OnCartListClickListener {
        fun onCartClick(position: Int, item: CartItem)
        fun onIncDecClick(position: Int, item: CartItem, qty: String)
        fun onErrorClick(position: Int, item: CartItem, message: String)
    }

}