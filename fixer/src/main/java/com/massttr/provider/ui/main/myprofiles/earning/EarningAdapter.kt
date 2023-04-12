package com.massttr.provider.ui.main.myprofiles.earning

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.Earning
import com.massttr.user.utils.changeDateFormat
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemEarningBinding
import java.util.*

class EarningAdapter : BaseAdapter<ListItemEarningBinding, Earning>(R.layout.list_item_earning) {

    override fun setClickableView(binding: ListItemEarningBinding): List<View?> {
        return listOf(binding.tvTime)
    }

    override fun onBind(
        binding: ListItemEarningBinding,
        position: Int,
        item: Earning,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            tvDate.text = changeDateFormat(
                item.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "d MMM"
            ).uppercase(Locale.getDefault())
            tvJobTitle.text = item.job?.title
            tvPayout.text = (item.job_price).plus(" QD")
            tvTime.text = changeDateFormat(
                item.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "hh:mm a"
            ).uppercase(Locale.getDefault())
        }
    }
}