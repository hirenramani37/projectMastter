package com.massttr.user.ui.main.myprofile.order_history.order_details

import android.content.Context
import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.JobImage
import com.massttr.user.R
import com.massttr.user.databinding.ListItemOrderHistoryJobPhotoBinding
import com.massttr.user.utils.loadImages

class OrderDetailsJobPhotoAdapter(val context: Context) :
    BaseAdapter<ListItemOrderHistoryJobPhotoBinding, JobImage>(R.layout.list_item_order_history_job_photo) {
    override fun setClickableView(binding: ListItemOrderHistoryJobPhotoBinding): List<View?> {
        return listOf(binding.ivPhotoTask)
    }

    override fun onBind(
        binding: ListItemOrderHistoryJobPhotoBinding,
        position: Int,
        item: JobImage,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            when (item.type) {
                1 -> context.loadImages(item.image, ivPhotoTask, R.drawable.img_placeholder)
                2 -> context.loadImages(item.image, ivPhotoTask, R.drawable.img_placeholder)
                else -> context.loadImages(item.image, ivPhotoTask, R.drawable.img_placeholder)
            }
        }
    }
}