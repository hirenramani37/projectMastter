package com.massttr.user.ui.main.notification


import android.content.Context
import android.text.format.DateUtils
import android.view.View
import com.common.base.BaseStickyHeaderAdapter
import com.common.data.network.model.NotificationResponse
import com.common.data.prefs.SharedPref
import com.massttr.user.BuildConfig
import com.massttr.user.MyApp
import com.massttr.user.R
import com.massttr.user.databinding.ListItemHederBinding
import com.massttr.user.databinding.ListItemNotificationBinding
import com.massttr.user.utils.getSystemDateFromUTCDate
import com.massttr.user.utils.gone
import com.massttr.user.utils.timeToConUTC
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val context: Context) :
    BaseStickyHeaderAdapter<ListItemHederBinding, ListItemNotificationBinding, NotificationResponse>(
        R.layout.list_item_heder,
        R.layout.list_item_notification
    ) {

    val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    private val dateFormatOld = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    )
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun setClickableView(binding: ListItemNotificationBinding): List<View?> =
        listOf(binding.root)

    override fun onBind(
        binding: ListItemNotificationBinding,
        position: Int,
        item: NotificationResponse,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            if (pref.isLanguage == true) {
                tvNotificationName.text = item.title
                tvNotificationDesc.text = item.message
            } else {
                tvNotificationName.text = item.ar_title
                tvNotificationDesc.text = item.ar_message
            }
            tvDate.text = dateFormat.format(dateFormatOld.parse(timeToConUTC(item.created_at)) as Date)

            if((displayList.size) -1 == position) viewDivider.gone()
        }
    }

    override fun includeItem(query: CharSequence?, item: NotificationResponse): Boolean = true

    override fun getMyHeaderId(position: Int): Long {
        if (displayList[position].headerId ?: -1 < 0)
            displayList[position].headerId = getChatHeaderId(displayList[position].created_at)
        return displayList[position].headerId ?: 0
    }

    override fun onBindHeader(headerBinding: ListItemHederBinding, position: Int) {
        if (displayList[position].headerValue.isNullOrEmpty())
            displayList[position].headerValue = getChatHeaderValue(displayList[position].created_at)

        Timber.e("list: ${displayList[position]}")
        Timber.e("listData: ${getChatHeaderValue(displayList[position].created_at)}")

        headerBinding.tvDay.text = displayList[position].headerValue
    }

    private fun getChatHeaderId(utcDateStr: String?): Long {
        if (utcDateStr.isNullOrEmpty()) return 0L

        val sysDateStr = utcDateStr.getSystemDateFromUTCDate(BuildConfig.UtcFormat)
        val date =
            SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) as Date

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val onlyDateString = sdf.format(date)
        val onlyDate = sdf.parse(onlyDateString) as Date
        return onlyDate.time
    }

    private fun getChatHeaderValue(utcDateStr: String?): String {
        if (utcDateStr.isNullOrEmpty()) return ""

        val sysDateStr = utcDateStr.getSystemDateFromUTCDate(BuildConfig.UtcFormat)
        val sysDate = SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) as Date

        val currentDate = Date()
        val diff = currentDate.time - sysDate.time
        val totalHours = diff / (1000 * 60 * 60)

        val currentCal = Calendar.getInstance()
        val lastSeenDateCal = Calendar.getInstance()
        lastSeenDateCal.time = sysDate

        val currentSysDate = currentCal.get(Calendar.DATE)
        val lastSeenDate = lastSeenDateCal.get(Calendar.DATE)

        return if (totalHours < 24 && lastSeenDate == currentSysDate) {
            context.getString(R.string.today)
        } else if (DateUtils.isToday(sysDate.time + DateUtils.DAY_IN_MILLIS)) {
            context.getString(R.string.yesterday)
        } else {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            Timber.e("date: ${sdf.format(sysDate)}")
            sdf.format(sysDate)
        }
    }
}
