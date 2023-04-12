package com.massttr.user.ui.main.home.taskrequest

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Subcategory
import com.massttr.user.R
import com.massttr.user.databinding.ListItemCategoryBinding

class TaskRequestAdapter :
    BaseAdapter<ListItemCategoryBinding, Subcategory>(R.layout.list_item_category) {

    override fun setClickableView(binding: ListItemCategoryBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemCategoryBinding,
        position: Int,
        item: Subcategory,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            tvCategoryTitle.text = item.en_name
            view.setOnClickListener {
                removeItemAt(position)
            }
        }
    }
}