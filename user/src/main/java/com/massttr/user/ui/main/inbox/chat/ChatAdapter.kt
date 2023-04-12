package com.massttr.user.ui.main.inbox.chat

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import coil.load
import com.bumptech.glide.Glide
import com.common.base.BaseAdapter
import com.common.base.BaseStickyHeaderAdapter
import com.common.data.prefs.SharedPref
import com.massttr.user.utils.getMessageTime
import com.massttr.user.utils.getSystemDateFromUTCDate
import com.massttr.user.BuildConfig
import com.massttr.user.MyApp
import com.massttr.user.R
import com.massttr.user.chat.ChatMessage
import com.massttr.user.databinding.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ChatAdapter(val context: Context) :
    BaseStickyHeaderAdapter<LayoutChatHeaderBinding, ViewDataBinding, ChatMessage>(
        R.layout.layout_chat_header,
        R.layout.list_item_chat_none
    ), BaseAdapter.ILayoutSelector {

    companion object {
        const val PAYLOAD_STATUS = "P_STATUS"
    }

    private val pref: SharedPref by lazy { MyApp.getInstance().getPref() }

    init {
        setLayoutSelector(this)
    }

    override fun getMyHeaderId(position: Int): Long {
        if (displayList[position].headerId ?: -1 < 0)
            displayList[position].headerId = getChatHeaderId(displayList[position].created_date)
        return displayList[position].headerId ?: 0
    }

    override fun onBindHeader(headerBinding: LayoutChatHeaderBinding, position: Int) {
        if (displayList[position].headerValue.isNullOrEmpty())
            displayList[position].headerValue =
                getChatHeaderValue(displayList[position].created_date)

        headerBinding.tvHeader.text = displayList[position].headerValue
    }

    override fun getItemViewType(position: Int): Int = getItemViewType(displayList[position])


    override fun selectLayout(viewType: Int): Int {
        return when (viewType) {
            MessageType.TEXT_SENDER.type -> R.layout.list_item_text_sender
            MessageType.TEXT_RECEIVER.type -> R.layout.list_item_text_receiver

            MessageType.IMAGE_SENDER.type -> R.layout.list_item_image_sender
            MessageType.IMAGE_RECEIVER.type -> R.layout.list_item_image_receiver

            MessageType.VIDEO_SENDER.type -> R.layout.list_item_video_sender
            MessageType.VIDEO_RECEIVER.type -> R.layout.list_item_video_receiver

            MessageType.CHANGE_PRICE_SENDER.type -> R.layout.list_item_change_price_sender

            else -> R.layout.list_item_chat_none
        }
    }

    override fun setClickableView(binding: ViewDataBinding): List<View?> {
        return when (binding) {
//            is ListItemTextSenderBinding -> listOf(binding.tvMyText)
            is ListItemImageSenderBinding -> listOf(binding.ivMyImage)
            is ListItemImageReceiverBinding -> listOf(binding.ivOtherImage)
            is ListItemVideoSenderBinding -> listOf(binding.ivMyVideo)
            is ListItemVideoReceiverBinding -> listOf(binding.ivOtherVideo)
            else -> listOf()
        }
    }

    override fun includeItem(query: CharSequence?, item: ChatMessage): Boolean {
        /**
         * it will show all the item
         * */
        return false
    }

    override fun onBind(
        binding: ViewDataBinding,
        position: Int,
        item: ChatMessage,
        payloads: MutableList<Any>?
    ) {
        if (payloads.isNullOrEmpty()) {
            when (binding) {
                is ListItemTextSenderBinding -> {
                    binding.tvMyText.text = item.message
                    binding.tvMyTime.text = getMessageTime(item.created_date)
                    binding.ivMyStatus.setReadStatus(item.read_status)
                }
                is ListItemTextReceiverBinding -> {
                    binding.tvOtherText.text = item.message
                    binding.tvOtherTime.text = getMessageTime(item.created_date)
                }
                is ListItemImageSenderBinding -> {
                    binding.ivMyImage.load(item.message){
                        placeholder(R.drawable.img_placeholder)
                        error(R.drawable.img_placeholder)
                    }
                    binding.tvMyTime.text = getMessageTime(item.created_date)
                    binding.ivMyStatus.setReadStatus(item.read_status)
                }
                is ListItemImageReceiverBinding -> {
                    binding.ivOtherImage.load(item.message){
                        placeholder(R.drawable.img_placeholder)
                        error(R.drawable.img_placeholder)
                    }
                    binding.tvOtherTime.text = getMessageTime(item.created_date)
                }
                is ListItemVideoSenderBinding -> {
                    Glide.with(context).load(item.message).thumbnail(0.1f)
                        .placeholder(R.drawable.img_placeholder)
                        .into(binding.ivMyVideo)
                    binding.tvMyTime.text = getMessageTime(item.created_date)
                    binding.ivMyStatus.setReadStatus(item.read_status)
                }
                is ListItemVideoReceiverBinding -> {
                    Glide.with(context).load(item.message).thumbnail(0.1f)
                        .placeholder(R.drawable.img_placeholder)
                        .into(binding.ivOtherVideo)
                   // binding.ivOtherVideo.load(item.message)
                    binding.tvOtherTime.text = getMessageTime(item.created_date)
                }
                is ListItemChangePriceSenderBinding -> {
                    binding.tvChangePrice.text = ("${item.message} QD")
                    binding.tvMyTime.text = getMessageTime(item.created_date)
                }
            }
        } else {
            payloads.forEach {
                when (it) {
                    PAYLOAD_STATUS ->
                        when (binding) {
                            is ListItemTextSenderBinding ->  binding.ivMyStatus.setReadStatus(item.read_status)
                            is ListItemImageSenderBinding -> binding.ivMyStatus.setReadStatus(item.read_status)
                            is ListItemVideoSenderBinding -> binding.ivMyStatus.setReadStatus(item.read_status)
                        }
                }
            }
        }
    }

    private fun getChatHeaderId(utcDateStr: String?): Long {
        if (utcDateStr.isNullOrEmpty()) return 0L

        val sysDateStr = utcDateStr.getSystemDateFromUTCDate(BuildConfig.UtcFormat)
        val date =
            SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) ?: Date()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val onlyDateString = sdf.format(date)
        val onlyDate = sdf.parse(onlyDateString) ?: Date()

        return onlyDate.time
    }


    private fun getChatHeaderValue(utcDateStr: String?): String {
        if (utcDateStr.isNullOrEmpty()) return ""

        val sysDateStr = utcDateStr.getSystemDateFromUTCDate(BuildConfig.UtcFormat)
        val sysDate =
            SimpleDateFormat(BuildConfig.UtcFormat, Locale.getDefault()).parse(sysDateStr) ?: Date()

        val currentDate = Date()
        val diff = currentDate.time - sysDate.time
        val totalHours = diff / (1000 * 60 * 60)

        val currentCal = Calendar.getInstance()
        val lastSeenDateCal = Calendar.getInstance()
        lastSeenDateCal.time = sysDate

        val currentSysDate = currentCal.get(Calendar.DATE)
        val lastSeenDate = lastSeenDateCal.get(Calendar.DATE)

        return if (totalHours < 24 && lastSeenDate == currentSysDate) {
            "Today"
        } else if (DateUtils.isToday(sysDate.time + DateUtils.DAY_IN_MILLIS)) {
            "Yesterday"
        } else {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(sysDate)
        }
    }


    private fun getItemViewType(chatMessage: ChatMessage): Int {

        val isSender = chatMessage.sender_id == pref.userInfo?.id ?: false

        return when (chatMessage.message_type) {
            MessageType.TEXT.type -> if (isSender) MessageType.TEXT_SENDER.type else MessageType.TEXT_RECEIVER.type
            MessageType.PHOTO.type -> if (isSender) MessageType.IMAGE_SENDER.type else MessageType.IMAGE_RECEIVER.type
            MessageType.VIDEO.type -> if (isSender) MessageType.VIDEO_SENDER.type else MessageType.VIDEO_RECEIVER.type
            MessageType.CHANGE_PRICE.type -> if (isSender) MessageType.CHANGE_PRICE_SENDER.type else MessageType.NONE.type
            else -> MessageType.NONE.type
        }
    }

    private fun ImageView.setReadStatus(readStatus: Int?) {
        when (readStatus) {
            ReadStatus.PENDING.status -> setImageResource(R.drawable.ic_pending)
            ReadStatus.SENT.status -> setImageResource(R.drawable.ic_send)
            ReadStatus.DELIVERED.status -> setImageResource(R.drawable.ic_no_read)
            ReadStatus.READ.status -> setImageResource(R.drawable.ic_sent_msg)
            else -> setImageResource(R.drawable.ic_pending)
        }
    }
}