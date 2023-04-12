package com.massttr.provider.ui.main.myprofiles.manages.documents

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.common.base.BaseAdapter
import com.common.data.network.model.Document
import com.common.data.network.model.GetDocumentType
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemDocumentsBinding

class RecordAdapter(val context: Context,val getDocumentType: GetDocumentType) : BaseAdapter<ListItemDocumentsBinding, Document>(R.layout.list_item_documents) {

    override fun setClickableView(binding: ListItemDocumentsBinding): List<View?> {
        return listOf(binding.imgDelete)
    }

    override fun onBind(
        binding: ListItemDocumentsBinding,
        position: Int,
        item: Document,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            if(item.type==getDocumentType.id){
                tvDocumentName.text = getDocumentType.en_name
            }

            imgDelete.isVisible = item.is_verify != 1

        }

    }

}