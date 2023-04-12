//package com.massttr.provider.ui.main.myprofiles.manages.documents
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.common.data.network.model.Document
//import com.massttr.provider.R
//import com.massttr.provider.databinding.LitsItemManageDocumentsBinding
//
//
//class SubDocumentListAdapter(
//    private val subCategoryInfoList: List<Document>,
//    val parentPosition: Int = 0
//) :
//    RecyclerView.Adapter<SubDocumentListAdapter.MyViewHolder>() {
//
//    private var mListener: OnItemClickLister? = null
//
//    interface OnItemClickLister {
//        fun onSubItemClick(position: Int, parentPosition: Int)
//    }
//
//    fun setOnItemClickListener(listener: OnItemClickLister?) {
//        mListener = listener
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding: LitsItemManageDocumentsBinding =
//            DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.lits_item_manage_documents,
//                parent,
//                false
//            )
//        return MyViewHolder(binding, mListener)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val subItem: Document = subCategoryInfoList[position]
//        holder.binding.lblDocumentName.text = subItem.name
//    }
//
//    override fun getItemCount() = subCategoryInfoList.size
//
//    inner class MyViewHolder(
//        val binding: LitsItemManageDocumentsBinding,
//        mListener: OnItemClickLister?
//    ) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            itemView.setOnClickListener {
//                val position = absoluteAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    mListener?.onSubItemClick(position, parentPosition)
//                }
//            }
//        }
//    }
//}