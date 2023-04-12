package com.massttr.provider.ui.main.myprofiles.completedTasks

import android.view.View
import com.common.base.BaseAdapter
import com.common.data.network.model.CompleteJobMainData
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemCompletedTasksBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CompletedTasksAdapter :
    BaseAdapter<ListItemCompletedTasksBinding, CompleteJobMainData>(R.layout.list_item_completed_tasks) {
    override fun setClickableView(binding: ListItemCompletedTasksBinding): List<View?> =
        listOf(binding.btnDownloadReceipt)

    override fun onBind(
        binding: ListItemCompletedTasksBinding,
        position: Int,
        item: CompleteJobMainData,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            try {
                tvUserName.text = item.user?.full_name
                tvOrderPrice.text = item.price.plus(" QD")
                tvOrderDescription.text = item.description
                tvCompletedTasksDate.text = timeToConUTC(item.completed_task_datetime ?: "")
                //tvCompletedTasksDate.text = item.appointment_date.plus(" " + item.appointment_time)
                // Timber.e("complete date: ${timeToConUTC(item.completed_task_datetime?:"")}")
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun timeToConUTC(time: String): String {
        val df = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(time)
        df.timeZone = TimeZone.getDefault()
        return df.format(date as Date)
    }
}
