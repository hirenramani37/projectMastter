package com.massttr.provider.ui.main.myprofiles.manages.documents

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.common.base.BaseAdapter
import com.common.data.network.model.Document
import com.common.data.network.model.GetDocumentType
import com.common.data.prefs.SharedPref
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.databinding.LitsItemManageDocumentsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class NationalIdFrontAdapter(val context: Context,
                             private val onChildClick: (adapter : RecordAdapter, item: Document, index: Int) -> Unit) :
    BaseAdapter<LitsItemManageDocumentsBinding, GetDocumentType>(R.layout.lits_item_manage_documents) {

    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    override fun setClickableView(binding: LitsItemManageDocumentsBinding): List<View?> {
        return listOf(binding.btnAddFront)
    }


    override fun onBind(
        binding: LitsItemManageDocumentsBinding,
        position: Int,
        item: GetDocumentType,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if (pref.isArabic == true) {
                tvDocumentName.text = item.ar_name
            } else {
                tvDocumentName.text = item.en_name
            }

            val recordAdapter = RecordAdapter(context, item)
            rvDoc.adapter = recordAdapter
            if (!item.documents.isNullOrEmpty()) {
                val documents = item.documents.filter { it.type == item.id }
                recordAdapter.addAll(documents)
                btnAddFront.isVisible = item.max_doc != documents.size
            }else{
                btnAddFront.isVisible = true
            }

            recordAdapter.setItemClickListener { view, index, document ->
                if(document.type == item.id){
                    onChildClick.invoke(recordAdapter, document, index)
                }
            }
        }
    }

}

