package com.massttr.user.ui.main.home.taskrequest.task_details

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Subcategory
import com.massttr.user.R
import com.massttr.user.databinding.ListItemTagBinding

class TaskTagAdapter : BaseAdapter<ListItemTagBinding, Subcategory>(R.layout.list_item_tag) {
    override fun setClickableView(binding: ListItemTagBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemTagBinding,
        position: Int,
        item: Subcategory,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            tvCategoryTitle.text = item.en_name
        }
    }
}
