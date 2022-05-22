package com.greenflames.myzebeel.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.greenflames.myzebeel.models.category.CategoryModel
import com.greenflames.myzebeel.ui.fragments.ProductMainFragment


/*
 *Created by Adithya T Raj on 29-04-2021
*/

class MainFragmentAdapter(
        fragment: FragmentActivity,
        private val productIdList: MutableList<CategoryModel> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = productIdList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ProductMainFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_PRODUCT_ID, productIdList[position].id)
        }
        return fragment
    }

    companion object {
        const val ARG_PRODUCT_ID = "arg_product_id"
    }

}