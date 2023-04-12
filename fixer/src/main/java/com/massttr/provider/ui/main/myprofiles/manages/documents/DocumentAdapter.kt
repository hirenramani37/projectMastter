package com.massttr.provider.ui.main.myprofiles.manages.documents

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.common.base.BaseAdapter
import com.common.data.network.model.Document
import com.common.data.network.model.DocumentData
import com.massttr.user.utils.gone
import com.massttr.user.utils.visible
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemDocumentBinding
import com.massttr.provider.databinding.ListItemDocumentDataBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.FixerRegistrationActivity

class DocumentAdapter(val context: Context) :
    BaseAdapter<ListItemDocumentDataBinding, DocumentData>(R.layout.list_item_document_data) {

    lateinit var recordAdapter: RecordAdapter

    override fun setClickableView(binding: ListItemDocumentDataBinding): List<View?> {
        return listOf(binding.btnAddDocument)
    }

    override fun onBind(
        binding: ListItemDocumentDataBinding,
        position: Int,
        item: DocumentData,
        payloads: MutableList<Any>?
    ) {
        binding.run {
          //  recordAdapter = RecordAdapter(context)
            tvDocumentHeader.text = item.headerTitle
            tvNoDocText.text = item.noDocFoundMsg
            rvDocList.adapter = recordAdapter
            recordAdapter.addAll(item.documentList)

            if (item.documentList.isEmpty()) {
                llNoRecordFound.visible()
                rvDocList.gone()
            } else {
                llNoRecordFound.gone()
                rvDocList.visible()
            }

            when(item.type){
                1->{
                    btnAddDocument.isVisible = displayList.size != item.max_doc
                }
                2->{
                    btnAddDocument.isVisible = displayList.size != item.max_doc
                }
                3->{
                    btnAddDocument.isVisible = displayList.size != item.max_doc
                }
                4->{
                    btnAddDocument.isVisible = displayList.size != item.max_doc
                }
            }


            recordAdapter.setItemClickListener { view, pos, document ->
                when (view.id) {
                    R.id.imgDelete -> {
                        (context as FixerRegistrationActivity).onClickDelete(document.id)
                    }
                }
            }
        }
    }
}

//class RecordAdapter(val context: Context) : BaseAdapter<ListItemDocumentBinding, Document>(R.layout.list_item_document) {
//
//    override fun setClickableView(binding: ListItemDocumentBinding): List<View?> {
//        return listOf(binding.imgDelete)
//    }
//
//    override fun onBind(
//        binding: ListItemDocumentBinding,
//        position: Int,
//        item: Document,
//        payloads: MutableList<Any>?
//    ) {
//
//        // type 1 national id font
//        binding.run {
//            when(item.type){
//                1->{
//
//                    lblDocumentName.text = context.getString(R.string.national_id_front)
//                }
//                2->{
//                    lblDocumentName.text =  context.getString(R.string.national_id_back)
//                }
//                3->{
//                    lblDocumentName.text =  context.getString(R.string.residence_card)
//                }
//                4->{
//                    lblDocumentName.text = context.getString(R.string.resume)
//                }
//            }
//
//
//
//
//        }
//
//    }
//
//}