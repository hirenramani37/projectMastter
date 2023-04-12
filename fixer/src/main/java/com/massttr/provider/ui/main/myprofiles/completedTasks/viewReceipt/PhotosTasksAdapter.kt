package com.massttr.provider.ui.main.myprofiles.completedTasks.viewReceipt

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.JobImage
import com.common.data.network.model.PhotosTasks
import com.makeramen.roundedimageview.RoundedImageView
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemPhotosTaskBinding
import timber.log.Timber


class PhotosTasksAdapter :
    BaseAdapter<ListItemPhotosTaskBinding, JobImage>(R.layout.list_item_photos_task) {
    override fun setClickableView(binding: ListItemPhotosTaskBinding): List<View?> =
        listOf(binding.ivPhotosTasks)

    override fun onBind(
        binding: ListItemPhotosTaskBinding,
        position: Int,
        item: JobImage,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            loadImage(item.image, ivPhotosTasks)
        }
    }

    private fun loadImage(image: String, civProfile: RoundedImageView) {
        civProfile.load(image) {
            placeholder(R.drawable.img_placeholder)
            error(R.drawable.img_placeholder)
        }
    }
}
