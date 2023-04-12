package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list

import android.view.View
import coil.load
import com.common.base.BaseAdapter
import com.common.data.network.model.Fixer
import com.common.data.network.model.NearByFixersData
import com.massttr.user.R
import com.massttr.user.databinding.ListItemAvailableFixerBinding
import timber.log.Timber

class FixerListAdapter :
    BaseAdapter<ListItemAvailableFixerBinding, NearByFixersData>(R.layout.list_item_available_fixer) {
    override fun setClickableView(binding: ListItemAvailableFixerBinding): List<View?> =
        listOf(binding.root, binding.btnChat, binding.mainView)

    override fun onBind(
        binding: ListItemAvailableFixerBinding,
        position: Int,
        item: NearByFixersData,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            civProfile.load(item.fixer.profile_picture) {
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvFixerRating.text = item.fixer.avg_rating.toString()
            tvFixerName.text = item.fixer.full_name
            tvJobContent.text = item.fixer.description
            tvCompletedJobs.text = item.fixer.total_completed_jobs.toString()
            tvDistance.text = item.distance.plus(" km")
        }
    }
}