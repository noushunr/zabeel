package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.models.address.AddressModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
 *Created by Adithya T Raj on 04-05-2021
*/

class AddressAdapter(private val mListener: OnAddressClickListener)
    : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private val diffCallBack = object : DiffUtil.ItemCallback<AddressModel>() {
        override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    fun submitList(list: List<AddressModel>) {
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

        private val nameTextView: TextView = itemView.findViewById(R.id.list_address_name_txt)
        private val addressTextView: TextView = itemView.findViewById(R.id.list_address_full_txt)
        private val editButton: Button = itemView.findViewById(R.id.list_address_edit_btn)
        private val deliverButton: Button = itemView.findViewById(R.id.list_address_deliver_btn)
        private val deleteImg: AppCompatImageView = itemView.findViewById(R.id.list_address_delete_img)

        fun bind(item: AddressModel, callBack: OnAddressClickListener) {

            val aCustomerName = StringBuilder()
            aCustomerName.append(item.firstname).append(" ").append(item.lastname)

            nameTextView.text = aCustomerName

            val street = StringBuilder()
            /*val streetArray : MutableList<String> = mutableListOf()
            if (item.street != null) {
                for (i in 0 until item.street.length()) {
                    streetArray.add(item.street.getString(i))
                }
            }*/

            for (streetName in item.street) {
                street.append(streetName).append(", ")
            }

            val country = item.region.region

            val mCity = StringBuilder()
            if (item.city != null) {
                mCity.append(item.city)
            }
            if (country != null) {
                mCity.append(", ").append(country)
            }
            if (item.postcode != null) {
                mCity.append(", ").append(item.postcode)
            }

            val mAddress = StringBuilder()
            mAddress.append(street).append(mCity).append("\nTelephone : ").append(item.telephone)

            addressTextView.text = mAddress

            editButton.setOnClickListener {
                callBack.onEditClick(adapterPosition, item)
            }

            deliverButton.setOnClickListener {
                callBack.onDeliverClick(adapterPosition, item)
            }

            deleteImg.setOnClickListener {
                callBack.onDeleteClick(adapterPosition, item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.list_address, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    interface OnAddressClickListener {
        fun onEditClick(position: Int, item: AddressModel)
        fun onDeliverClick(position: Int, item: AddressModel)
        fun onDeleteClick(position: Int, item: AddressModel)
    }

}