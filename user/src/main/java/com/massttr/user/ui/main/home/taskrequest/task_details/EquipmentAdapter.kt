package com.massttr.user.ui.main.home.taskrequest.task_details

import android.view.View
import com.common.base.BaseAdapter
import com.massttr.user.FixerList
import com.massttr.user.R
import com.massttr.user.databinding.ListItemEquipmentBinding

class EquipmentAdapter :
    BaseAdapter<ListItemEquipmentBinding, String>(R.layout.list_item_equipment) {
    override fun setClickableView(binding: ListItemEquipmentBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemEquipmentBinding,
        position: Int,
        item: String,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            tvCategoryTitle.text = item


        }
    }
}
