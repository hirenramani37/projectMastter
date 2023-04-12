package com.massttr.user.ui.main.myprofile.order_history

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.common.base.BaseAdapter
import com.common.data.network.model.OrderHistoryDetails
import com.massttr.user.utils.gone
import com.massttr.user.utils.visible
import com.massttr.user.R
import com.massttr.user.databinding.ListItemOrderHistoryBinding
import com.massttr.user.utils.loadImages


class OrderHistoryAdapter(val context: Context) :
    BaseAdapter<ListItemOrderHistoryBinding, OrderHistoryDetails>(R.layout.list_item_order_history) {
    override fun setClickableView(binding: ListItemOrderHistoryBinding): List<View?> =
        listOf(binding.root,
            binding.clMain,
            binding.tvDownload,
            binding.tvViewFixer,
            binding.tvEditPrice)

    override fun onBind(
        binding: ListItemOrderHistoryBinding,
        position: Int,
        item: OrderHistoryDetails,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            tvOrderName.text = item.title
            tvOrderDescription.text = item.description
            tvOrderPrice.text = (item.price).plus(" QD")
            tvOrderDate.text = item.appointment_date.plus(",".plus(item.appointment_time))
            tvViewFixer.text = item.status
            Glide.with(context)
                .load(item.banner_image)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(civProfile)
            if(position == itemCount - 1) viewDivider.gone()

            when (item.job_status_id) {
                //Accept
                2 -> {
                    tvEditPrice.visible()
                    tvViewFixer.text = context.getText(R.string.view_fixer)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.color_blue))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_blue_lite))
                }
                //Start
                3 -> {
                    tvEditPrice.gone()
                    tvDownload.visible()
                    tvViewFixer.text = context.getText(R.string.view_fixer)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.color_blue))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_green))
                }
                //End
                4 -> {
                    tvEditPrice.gone()
                    tvDownload.visible()
                    tvViewFixer.text = context.getText(R.string.completed)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_green))
                }
                //Complete Job
                5 -> {
                    tvEditPrice.gone()
                    tvDownload.visible()
                    tvViewFixer.text = context.getText(R.string.completed)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_green))
                }
                //User Cancel
                6 -> {
                    tvEditPrice.gone()
                    tvViewFixer.text = context.getText(R.string.cancel)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorRed))
                }
                //Pending
                else -> {
                    tvEditPrice.visible()
                    tvViewFixer.text = context.getText(R.string.pending)
                    tvViewFixer.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    tvViewFixer.backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_yellow))
                }
            }
        }
    }
}