package com.massttr.provider.ui.main.availableTasks

import android.content.Context
import android.view.View
import coil.load
import com.bumptech.glide.Glide
import com.common.base.BaseAdapter
import com.common.data.network.model.TaskList
import com.massttr.user.utils.changeDateFormat
import com.makeramen.roundedimageview.RoundedImageView
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemAvailableTaskBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AvailableTaskAdapter(private val context: Context) :
    BaseAdapter<ListItemAvailableTaskBinding, TaskList>(R.layout.list_item_available_task) {

    override fun setClickableView(binding: ListItemAvailableTaskBinding): List<View?> =
        listOf(binding.root, binding.tvKm)

    override fun onBind(
        binding: ListItemAvailableTaskBinding,
        position: Int,
        item: TaskList,
        payloads: MutableList<Any>?,
    ) {
        binding.run {

            Glide.with(context)
                .load(item.banner_image)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivImage)

            tvTitle.text = item.title
            tvDis.text = item.description
            tvPrice.text = (item.price).plus(" QD")

            val appointDate = item.appointment_date
            val format1 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val dt1: Date = format1.parse(appointDate)?:Date()
            val format2: DateFormat = SimpleDateFormat("E", Locale.getDefault())
            val finalDay: String = format2.format(dt1)

            tvDate.text =  ("$finalDay " + changeDateFormat(item.appointment_date, "dd MMMM yyyy", "dd MMM")
                    .plus(", ".plus(changeDateFormat(item.appointment_time, "hh:mm a", "hh:mm"))))
            //tvKm.text = item.distance.toString()

//            if (item.distance.toInt() == 0) {
//                tvKm.text = String.format("%.2f", item.distance.toString().toDouble() * 1000.0).plus(" m")
//            } else {
//                tvKm.text = String.format("%.2f", item.distance).plus(" km")
//            }
            tvKm.text = String.format("%.2f", item.distance).plus(" km")
        }
    }
}