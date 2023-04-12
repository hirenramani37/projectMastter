package com.massttr.provider.ui.main.myprofiles.inbox

import android.view.View
import androidx.core.view.isVisible
import coil.load
import com.common.base.BaseAdapter
import com.massttr.user.utils.getMessageTime
import com.massttr.provider.BuildConfig
import com.massttr.provider.R
import com.massttr.provider.chat.Inbox
import com.massttr.provider.databinding.ListItemInboxBinding

class InboxAdapter : BaseAdapter<ListItemInboxBinding, Inbox>(R.layout.list_item_inbox) {

    companion object {
        const val PAYLOAD_TYPING = "P_TYPING"
        const val PAYLOAD_INBOX_ITEM = "P_INBOX_ITEM"
    }

    override fun setClickableView(binding: ListItemInboxBinding): List<View?> =
        listOf(binding.root, binding.clMain)

    override fun onBind(
        binding: ListItemInboxBinding,
        position: Int,
        item: Inbox,
        payloads: MutableList<Any>?
    ) {
        binding.run {

            if (payloads.isNullOrEmpty()) {
                civProfile.load(BuildConfig.BaseUrlStorage + item.chat_user_avatar)
                tvFixerWorkName.text = item.job_title
                tvName.text = item.chat_user_name
                tvLastMessage.text = item.last_message
                tvDate.text = getMessageTime(item.last_message_date)
                tvCount.isVisible = item.unread_count > 0
                tvCount.text = item.unread_count.toString()
                ivIsOnline.isVisible = item.is_online
            } else {
                payloads.forEach {
                    when (it) {
                        is Pair<*, *> -> {
                            if (it.first == PAYLOAD_TYPING) {
                                val status = it.second as Boolean
                                if (status)
                                    tvLastMessage.text = root.context.getString(R.string.typing)
                                else
                                    tvLastMessage.text = item.last_message
                            }
                        }
                        PAYLOAD_INBOX_ITEM -> {
                            tvLastMessage.text = item.last_message
                            tvCount.isVisible = item.unread_count > 0
                            tvCount.text = item.unread_count.toString()
                        }
                    }
                }
            }

        }
    }
}