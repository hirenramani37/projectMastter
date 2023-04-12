package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.SelectPhotos
import com.massttr.user.R
import com.massttr.user.databinding.ListItemJobPhotoBinding
import timber.log.Timber
import java.io.File

class FixerTaskPhotoAdapter :
    BaseAdapter<ListItemJobPhotoBinding, File>(R.layout.list_item_job_photo) {
    override fun setClickableView(binding: ListItemJobPhotoBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemJobPhotoBinding,
        position: Int,
        item: File,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            Timber.e("file: $item")
            ivJobPhoto.load(item) {
                placeholder(R.drawable.ic_photo_placeholder)
                error(R.drawable.ic_photo_placeholder)
            }
        }
    }
}
