package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Document
import com.massttr.user.R
import com.massttr.user.databinding.ListItemDocumentBinding

class DocumentAdapter :
    BaseAdapter<ListItemDocumentBinding, Document>(R.layout.list_item_document) {
    override fun setClickableView(binding: ListItemDocumentBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemDocumentBinding,
        position: Int,
        item: Document,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            tvDocumentList.text = item.name
        }
    }
}