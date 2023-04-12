package com.massttr.user.ui.main.home.taskrequest

import android.content.Context
import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.SelectPhotos
import com.massttr.user.utils.gone
import com.massttr.user.utils.loadImages
import com.massttr.user.utils.visible
import com.massttr.user.R
import com.massttr.user.databinding.ListItemSelectTaskPhotosBinding
class SelectPhotoTaskAdapter(val context: Context) :
    BaseAdapter<ListItemSelectTaskPhotosBinding, SelectPhotos>(R.layout.list_item_select_task_photos) {

    override fun setClickableView(binding: ListItemSelectTaskPhotosBinding): List<View?> =
        listOf(binding.root, binding.ivPhotoTask, binding.llSelectPhoto)

    override fun onBind(
        binding: ListItemSelectTaskPhotosBinding,
        position: Int,
        item: SelectPhotos,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            context.loadImages(item.selectPhoto, ivPhotoTask,R.drawable.ic_placeholder)
            if (position == 0) {
                ivPhotoTask.gone()
                llSelectPhoto.visible()
            }
        }
    }
}