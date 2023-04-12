package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile

import android.view.View
import coil.load
import com.common.data.network.model.PastFixes
import com.massttr.user.R
import com.massttr.user.databinding.ListItemJobPhotoBinding

class FixerTasksPhotoAdapter :
    com.common.base.BaseAdapter<ListItemJobPhotoBinding, PastFixes>(R.layout.list_item_job_photo) {
    override fun setClickableView(binding: ListItemJobPhotoBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemJobPhotoBinding,
        position: Int,
        item: PastFixes,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            ivJobPhoto.load(item.banner_image) {
                placeholder(R.drawable.ic_photo_placeholder)
                error(R.drawable.ic_photo_placeholder)
            }
        }
    }
}