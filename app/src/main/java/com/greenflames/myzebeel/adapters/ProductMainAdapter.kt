package com.greenflames.myzebeel.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.helpers.clearHtmlTag
import com.greenflames.myzebeel.models.products.ProductModel
import com.greenflames.myzebeel.network.Apis.PRODUCT_AD_IMG_BASE_URL
import com.greenflames.myzebeel.network.Apis.PRODUCT_IMG_BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList


/*
 *Created by Adithya T Raj on 03-05-2021
*/

class ProductMainAdapter(private val mListener: OnProductClickListener)
    : ListAdapter<ProductModel, RecyclerView.ViewHolder>(ProductsDiffCallback()) {

    private var oldList: MutableList<ProductModel> = mutableListOf()
    private var adsList: MutableList<String> = mutableListOf()

    companion object {
        private const val ITEM_VIEW_TYPE_PRODUCT = 0
        private const val ITEM_VIEW_TYPE_AD = 1
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    class ProductsDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }

    }

    //private val differ = AsyncListDiffer(this, diffCallBack)

    fun initalList(list: List<ProductModel>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                oldList.clear()
                oldList.addAll(list)
                submitList(oldList.toList())
            }
        }
    }

    fun updateList(list: List<ProductModel>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                oldList.addAll(list)
                submitList(oldList.toList())
            }
        }
    }

    fun updateAdList(list: List<String>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                adsList.clear()
                adsList.addAll(list)
            }
        }
    }

    private fun getProductRealPos(position: Int) : Int {//8
        if (adsList.size.equals(0) || position < 4) {
            return position
        } else {
            /*val p = adsList.size * 4 + (adsList.size - 1)//9
            if (position <= p) {
                return position - position / 5
            } else {
                return position - adsList.size
            }*/
            return position - position / 5
        }
    }

    private fun getAdsRealPos(position: Int) : Int {//9
        return (position + 1) / 5 - 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_AD -> AdsViewHolder.from(parent)
            ITEM_VIEW_TYPE_PRODUCT -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                try {
                    holder.bind(oldList[getProductRealPos(position)], mListener)
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            is AdsViewHolder -> {
                try {
                    //holder.bind(adsList[getAdsRealPos(position)])
                    holder.bind(adsList)
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }

    override fun getItemCount() : Int {
        if (adsList.size.equals(0) || oldList.size < 4) {
            return oldList.size
        } else {
            val maxAdCount = oldList.size / 4
            /*if (adsList.size > maxAdCount) {
                return oldList.size + maxAdCount
            } else {
                return oldList.size + adsList.size
            }*/
            return oldList.size + maxAdCount
        }
    }

    override fun getItemViewType(position: Int): Int {
        if ((position+1) % 5 == 0 && (position+1) != 1 && adsList.size > 0) {
            /*val p = adsList.size * 4 + (adsList.size - 1)
            if (position <= p) {
                return ITEM_VIEW_TYPE_AD
            }*/
            return ITEM_VIEW_TYPE_AD
        }
        return ITEM_VIEW_TYPE_PRODUCT
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.productName_fruits)
        private val availabilityTextView: TextView = itemView.findViewById(R.id.availability_fruits)
        private val priceTextView: TextView = itemView.findViewById(R.id.price_fruits)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageView_fruits)
        private val mainCardView: CardView = itemView.findViewById(R.id.relative_home_card)

        fun bind(item: ProductModel, callBack: OnProductClickListener) {
            nameTextView.text = item.name
            availabilityTextView.text = when (item.status) {
                "0" -> itemView.context.getString(R.string.not_available_stock)
                else ->itemView.context.getString(R.string.available_in_stock)
            }
            val price = item.price
            if (price != null) {
                var priceFloat = String.format("%.3f", price.toFloat())
                priceTextView.text = "KD ${priceFloat}"
            }
            val filePath = when (item.media_gallery_entries.size) {
                0 -> ""
                else -> item.media_gallery_entries[0].file
            }
            val imgUrl = PRODUCT_IMG_BASE_URL + filePath

            Glide.with(productImageView)
                    .load(imgUrl)
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .into(productImageView)

            mainCardView.setOnClickListener {
                callBack.onProductClick(adapterPosition, item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = layoutInflater.inflate(R.layout.home_product_card, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    class AdsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val adsImageView: AppCompatImageView = itemView.findViewById(R.id.list_ad_img)
        private val imageSlider: ImageSlider = itemView.findViewById(R.id.image_slider)

        fun bind(
            //item: String
            item: MutableList<String>
        ) {

            /*val newUrl = PRODUCT_AD_IMG_BASE_URL + item.clearHtmlTag()

            println(newUrl)

            Glide.with(adsImageView)
                .load(newUrl.trim())
                .into(adsImageView)*/

            val imageList = ArrayList<SlideModel>()

            for (item1 in item) {
                val newUrl = PRODUCT_AD_IMG_BASE_URL + item1.clearHtmlTag()
                imageList.add(SlideModel(newUrl.trim(), ScaleTypes.FIT))
            }

            imageSlider.setImageList(imageList)

        }

        companion object {
            fun from(parent: ViewGroup): AdsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_product_ads, parent, false)
                return AdsViewHolder(view)
            }
        }
    }

    interface OnProductClickListener {
        fun onProductClick(position: Int, item: ProductModel)
    }

}