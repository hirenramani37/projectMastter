package com.massttr.provider.ui.main.myUpcomingFix

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.common.base.BaseAdapter
import com.common.data.network.model.Job
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemUpComingFixBinding
import com.massttr.provider.ui.main.myUpcomingFix.taskDetails.EquipmentAdapter
import com.massttr.user.utils.visible
import timber.log.Timber

class MyUpcomingFixAdapter(val context: Context) : BaseAdapter<ListItemUpComingFixBinding, Job>(R.layout.list_item_up_coming_fix) {
    private var equipmentAdapter: EquipmentAdapter? = null
    override fun setClickableView(binding: ListItemUpComingFixBinding): List<View?> {
        return listOf(
            binding.btnStartTask,
            binding.btnCancelTask,
            binding.viewRoot,
            binding.btnChat,
            binding.btnEndTask
        )
    }

    override fun onBind(
        binding: ListItemUpComingFixBinding,
        position: Int,
        item: Job,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if (item.user != null) {
                tvFixerName.text = item.user.full_name ?: "fixer"
                tvJobContent.text = item.description ?: ""
                tvOrderDate.text = item.appointment_date ?: ""
                tvFixerPrice.text = item.price?.plus(" QD") ?: ""
                //tvStartJobTime.text = item.start_task_datetime

                Glide.with(context)
                    .load(item.banner_image)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(civProfile)
//                civProfile.load(item.banner_image) {
//                    placeholder(R.drawable.img_profile)
//                }
                equipmentAdapter = EquipmentAdapter()
                rvRequiredEqui.adapter = equipmentAdapter

                val equipment: List<String>? =
                    item.required_equipment?.split(",")?.toTypedArray()?.toList()

                if (equipment != null) {
                    Timber.e("equipment")
                    tvRequiredEqu.visible()
                    equipmentAdapter?.addAll(equipment)
                }

                if (item.status == "Job started" || item.status == "Job startede") {
                    btnCancelTask.isVisible = false
                    btnStartTask.isVisible = false
                    btnEndTask.isVisible = true
                    //tvStartJobTime.text = item.start_task_datetime
                    tvCompletedJobs.text = item.status
                } else {
                    btnStartTask.isVisible = true
                    btnCancelTask.isVisible = true
                    btnEndTask.isVisible = false
                }
            }
        }
    }
}
