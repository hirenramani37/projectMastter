package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.photoTask

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.PastFixes
import com.massttr.user.FixPhoto
import com.massttr.user.R
import com.massttr.user.databinding.ListItemJobPhotoBinding
import com.massttr.user.databinding.ListItemSelectTaskPhotosBinding

class PhotosTaskAdapter :
    BaseAdapter<ListItemSelectTaskPhotosBinding, PastFixes>(R.layout.list_item_select_task_photos) {
    override fun setClickableView(binding: ListItemSelectTaskPhotosBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemSelectTaskPhotosBinding,
        position: Int,
        item: PastFixes,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            ivPhotoTask.load(item.banner_image){
                error(R.drawable.ic_photo_placeholder)
                placeholder(R.drawable.ic_photo_placeholder)

            }
            /*ivJobPhoto.load(R.drawable.img_photos_task_2) {
                placeholder(R.drawable.ic_photo_placeholder)
                error(R.drawable.ic_photo_placeholder)
            }*/
        }
    }
 }
