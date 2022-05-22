package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.greenflames.myzebeel.MainApplication
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.models.address.AddressModel
import com.greenflames.myzebeel.models.wallet.WalletValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 01-08-2021
*/

class WalletAdapter(val clickListener: (WalletValues) -> Unit)
    : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    private val itemList = arrayListOf<WalletValues>()
    private var viewPosition: Int? = null
    private var context : Context? = null

    fun submitList(list: List<WalletValues>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainLayout.setOnClickListener {
            clickListener(itemList[position])
            viewPosition = position
            notifyDataSetChanged()
        }
        holder.bind(itemList[position])
        when (viewPosition) {
            position -> {
                holder.valueTextView.setBackgroundColor(Color.parseColor("#FF9800"))
                holder.valueTextView.setTextColor(Color.WHITE)
                holder.valueTextView.background =
                    ContextCompat.getDrawable(holder.valueTextView.context, R.drawable.orange_button_round_corner)
            }
            else -> {
                holder.valueTextView.setBackgroundColor(Color.WHITE)
                holder.valueTextView.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount() = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val valueTextView: TextView = itemView.findViewById(R.id.list_wallet_value_txt)
        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.list_wallet_mainLayout)

        fun bind(item: WalletValues) {
            valueTextView.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.list_wallet_values, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}