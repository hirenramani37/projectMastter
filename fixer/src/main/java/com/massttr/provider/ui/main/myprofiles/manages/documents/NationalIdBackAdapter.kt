//package com.massttr.provider.ui.main.myprofiles.manages.documents
//
//import android.content.Context
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.common.base.BaseAdapter
//import com.common.data.network.model.Document
//import com.common.data.network.model.GetDocumentType
//import com.common.data.prefs.SharedPref
//import com.massttr.provider.MyApp
//import com.massttr.provider.R
//import com.massttr.provider.databinding.ListItemGetDocumentTypeBinding
//import com.massttr.provider.databinding.LitsItemManageDocumentsBinding
//
//class NationalIdBackAdapter(val context: Context) :
//    BaseAdapter<LitsItemManageDocumentsBinding, Document>(R.layout.lits_item_manage_documents) {
//
//    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
//
//    override fun setClickableView(binding: LitsItemManageDocumentsBinding): List<View?> {
//        return listOf(binding.imgDelete)
//    }
//
//    override fun onBind(
//        binding: LitsItemManageDocumentsBinding,
//        position: Int,
//        item: Document,
//        payloads: MutableList<Any>?,
//    ) {
//        binding.run {
//            binding.imgDelete.isVisible = item.is_verify==0
//            if(pref.isArabic == true){
//                lblDocumentName.text = context.getString(R.string.national_id_back)
//            }else{
//                lblDocumentName.text = context.getString(R.string.national_id_back)
//            }
//        }
//    }
//}