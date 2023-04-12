package com.massttr.provider.ui.main.myUpcomingFix.taskDetails

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.PhotosTasks
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemPhotosTaskBinding

class FixerTaskPhotoAdapter :
    BaseAdapter<ListItemPhotosTaskBinding, PhotosTasks>(R.layout.list_item_photos_task) {
    override fun setClickableView(binding: ListItemPhotosTaskBinding): List<View?> {
        return listOf(binding.ivPhotosTasks)
    }

    override fun onBind(
        binding: ListItemPhotosTaskBinding,
        position: Int,
        item: PhotosTasks,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            item.photos?.let { ivPhotosTasks.setImageResource(it) }
        }
    }
}