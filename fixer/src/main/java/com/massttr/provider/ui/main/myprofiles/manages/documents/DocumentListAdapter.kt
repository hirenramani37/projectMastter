package com.massttr.provider.ui.main.myprofiles.manages.documents

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseAdapter
import com.common.data.network.model.Document
import com.common.data.network.model.GetDocumentType
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemGetDocumentTypeBinding
import com.massttr.provider.databinding.LitsItemManageDocumentsBinding

class DocumentListAdapter() :
    BaseAdapter<ListItemGetDocumentTypeBinding, GetDocumentType>(R.layout.list_item_get_document_type) {

    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    override fun setClickableView(binding: ListItemGetDocumentTypeBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(
        binding: ListItemGetDocumentTypeBinding,
        position: Int,
        item: GetDocumentType,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if(pref.isArabic == true){
                tvDocumentName.text = item.ar_name
            }else{
                tvDocumentName.text = item.en_name
            }
        }
    }
}