package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.models.wishlist.WishListItem
import com.greenflames.myzebeel.network.Apis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 06-05-2021
*/

class WishListAdapter(private val mListener: OnWishListClickListener)
    : RecyclerView.Adapter<WishListAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<WishListItem>() {
        override fun areItemsTheSame(oldItem: WishListItem, newItem: WishListItem): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: WishListItem, newItem: WishListItem): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<WishListItem>) {
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

        private val mainLayout: LinearLayout = itemView.findViewById(R.id.card_view)
        private val nameTextView: TextView = itemView.findViewById(R.id.text1_wishlist)
        private val priceTextView: TextView = itemView.findViewById(R.id.text2_wishlist)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageview_wishlist)

        fun bind(item: WishListItem, callBack: OnWishListClickListener) {
            if (item.product != null) {
                nameTextView.text = item.product?.name
                priceTextView.text = "KD ${item.product?.price}"

                if (item.product.custom_attributes != null) {
                    val customAttributesArrayList = item.product.custom_attributes
                    for (customAttributes in customAttributesArrayList) {
                        val attributeCode = customAttributes.attribute_code
                        if (attributeCode == "image") {
                            val imgUrl = Apis.PRODUCT_IMG_BASE_URL + customAttributes.value.toString()

                            Glide.with(productImageView)
                                    .load(imgUrl)
                                    .error(R.drawable.logo)
                                    .placeholder(R.drawable.logo)
                                    .into(productImageView)
                            break
                        }
                    }
                }
                mainLayout.setOnClickListener {
                    callBack.onItemClick(adapterPosition, item)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.wishlist_card, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface OnWishListClickListener {
        fun onItemClick(position: Int, item: WishListItem)
    }

}