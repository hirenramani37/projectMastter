package com.massttr.provider.ui.main.availableTasks.viewTask

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.ViewJobImage
import com.makeramen.roundedimageview.RoundedImageView
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemPhotosTaskBinding

class ViewTaskAdapter :
    BaseAdapter<ListItemPhotosTaskBinding, ViewJobImage>(R.layout.list_item_photos_task) {

    override fun setClickableView(binding: ListItemPhotosTaskBinding): List<View?> =
        listOf(binding.root, binding.ivPhotosTasks)

    override fun onBind(
        binding: ListItemPhotosTaskBinding,
        position: Int,
        item: ViewJobImage,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            when (item.type) {
                0 -> loadImage(item.image, ivPhotosTasks)
                2 -> loadImage(item.image, ivPhotosTasks)
                else -> loadImage(item.image, ivPhotosTasks)
            }
        }
    }

    private fun loadImage(image: String, civProfile: RoundedImageView) {
        civProfile.load(image) {
            placeholder(R.drawable.img_placeholder)
            error(R.drawable.img_placeholder)
        }
    }
}