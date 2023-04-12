package com.massttr.user.ui.main.home.taskrequest.subCategory

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Subcategory
import com.massttr.user.R
import com.massttr.user.databinding.ListItemSubCategoryBinding

class SubCategoryAdapter :
    BaseAdapter<ListItemSubCategoryBinding, Subcategory>(R.layout.list_item_sub_category) {
    override fun setClickableView(binding: ListItemSubCategoryBinding): List<View?> =
        listOf(binding.root, binding.clLayout)

    override fun onBind(
        binding: ListItemSubCategoryBinding,
        position: Int,
        item: Subcategory,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            tvHelpMoving.text = item.en_name
            if (item.isSelected)
                ivPlus.setImageResource(R.drawable.ic_check_bg)
            else
                ivPlus.setImageResource(R.drawable.ic_add_service)
        }
    }
}