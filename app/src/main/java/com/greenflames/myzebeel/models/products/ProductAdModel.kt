package com.greenflames.myzebeel.models.products

import com.google.gson.annotations.SerializedName


/*
 *Created by Adithya T Raj on 22-07-2021
*/

data class ProductAdModel (

    @SerializedName("id")
    var id: String,

    @SerializedName("view_type")
    var view_type: String? = null,

    @SerializedName("product")
    var product: ProductModel? = null,

    @SerializedName("ad")
    var ad: String? = null

)