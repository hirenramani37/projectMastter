package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer.give_review.rating

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.JobImage
import com.makeramen.roundedimageview.RoundedImageView
import com.massttr.user.R
import com.massttr.user.databinding.ListItemPhotosTaskBinding

class PhotosTaskAdapter :
    BaseAdapter<ListItemPhotosTaskBinding, JobImage>(R.layout.list_item_photos_task) {
    override fun setClickableView(binding: ListItemPhotosTaskBinding): List<View?> {
        return listOf()
    }

    override fun onBind(
        binding: ListItemPhotosTaskBinding,
        position: Int,
        item: JobImage,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            loadImage(item.image, ivPhotosTask)
        }
    }

    private fun loadImage(image: String, civProfile: RoundedImageView) {
        civProfile.load(image) {
            placeholder(R.drawable.ic_photo_placeholder)
            error(R.drawable.ic_photo_placeholder)
        }
    }
}