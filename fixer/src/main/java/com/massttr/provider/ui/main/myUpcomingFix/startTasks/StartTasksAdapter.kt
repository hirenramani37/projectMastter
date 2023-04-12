package com.massttr.provider.ui.main.myUpcomingFix.startTasks

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.common.base.BaseAdapter
import com.common.data.network.model.JobImage
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemFixerStartTaskPhotosBinding

class StartTasksAdapter(val context: Context) :
    BaseAdapter<ListItemFixerStartTaskPhotosBinding, JobImage>(R.layout.list_item_fixer_start_task_photos) {
    override fun setClickableView(binding: ListItemFixerStartTaskPhotosBinding): List<View?> {
        return listOf(binding.imgClose, binding.ivPhotosTasks)
    }

    override fun onBind(
        binding: ListItemFixerStartTaskPhotosBinding,
        position: Int,
        item: JobImage,
        payloads: MutableList<Any>?
    ) {
        binding.run {
//            if (position == 0) {
//                ivPhotosTasks.scaleType = ImageView.ScaleType.FIT_XY
//                imgClose.visibility = View.GONE
//            } else {
            ivPhotosTasks.scaleType = ImageView.ScaleType.CENTER_CROP
            ivPhotosTasks.background = null
//                ivPhotosTasks.load(item.image){
//                    placeholder(R.drawable.ic_photo)
//                }
            // tvPhotoAdd.visibility = View.GONE
            //}
            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivPhotosTasks)

        }
    }
}