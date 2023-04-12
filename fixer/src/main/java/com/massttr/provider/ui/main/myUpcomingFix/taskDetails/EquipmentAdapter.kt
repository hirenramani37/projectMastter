package com.massttr.provider.ui.main.myUpcomingFix.taskDetails

import android.view.View
import com.common.base.BaseAdapter
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemEquipmentBinding


class EquipmentAdapter :
    BaseAdapter<ListItemEquipmentBinding, String>(R.layout.list_item_equipment) {
    override fun setClickableView(binding: ListItemEquipmentBinding): List<View?> {
        return listOf()
    }

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
