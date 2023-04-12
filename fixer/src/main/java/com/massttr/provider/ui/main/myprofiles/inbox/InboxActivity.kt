package com.massttr.provider.ui.main.myprofiles.inbox

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.utils.*
import com.massttr.provider.R
import com.massttr.provider.chat.*
import com.massttr.provider.databinding.ActivityInboxBinding
import com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat.ChatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class InboxActivity : BaseActivity<ActivityInboxBinding>(R.layout.activity_inbox),
    View.OnClickListener {
    private lateinit var inboxAdapter: InboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupUI()
        initBusEvent()
        clickListener()
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this@InboxActivity)
        inboxAdapter.setItemClickListener { view, _, inbox ->
            when (view.id) {
                R.id.clMain -> startActivity<ChatActivity>(ChatActivity.INBOX to inbox)
            }
        }
    }

    private fun init() {
        inboxAdapter = InboxAdapter()
        binding.rvInbox.setAdapter(inboxAdapter, binding.viewNoFound)

        FixerSocketService.getInstance()?.sendListChats(0)
    }

    private fun setupUI() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.inbox)
        }
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey(BUS_EVENT_INBOX_LIST)) {
                val inboxList: ArrayList<Inbox>? = it.getParcelableArrayList(BUS_EVENT_INBOX_LIST)
                inboxList?.let { inboxAdapter.addAll(inboxList) }
            }

            if (it.containsKey(BUS_EVENT_ONLINE)) {
                val online = it.getParcelable<Online>(BUS_EVENT_ONLINE)
                val index = inboxAdapter.displayList.indexOfLast { inbox -> inbox.chat_user_id == online?.user_id }
                if (index != -1)
                    inboxAdapter.displayList[index].is_online = online?.status == 1
            }

            if (it.containsKey(BUS_EVENT_TYPING)) {
                val typing = it.getParcelable<Typing>(BUS_EVENT_TYPING)
                val index = inboxAdapter.displayList.indexOfLast { inbox -> inbox.chat_id == typing?.chat_id }
                if (index != -1) {
                    inboxAdapter.displayList[index].is_online = true
                    inboxAdapter.notifyItemChanged(index, InboxAdapter.PAYLOAD_TYPING to (typing?.typing == 1))
                }
            }

            if (it.containsKey(BUS_EVENT_MESSAGE_RECEIVED_INBOX)) {
                val chatMessage: ChatMessage? = it.getParcelable(BUS_EVENT_MESSAGE_RECEIVED_INBOX)

                chatMessage?.let {
                    val index = inboxAdapter.displayList.indexOfFirst { inbox -> inbox.chat_id == chatMessage.chat_id }
                    if (index != -1) {
                        if (chatMessage.sender_id != pref.userInfo?.id) {
                            inboxAdapter.displayList[index].unread_count += 1
                        }
                        inboxAdapter.displayList[index].last_message = chatMessage.message
                        inboxAdapter.displayList[index].last_message_date = chatMessage.created_date
                        if (index == 0) {
                            inboxAdapter.notifyItemChanged(index, InboxAdapter.PAYLOAD_INBOX_ITEM)
                        } else {
                            inboxAdapter.displayList.sortByDescending { it.last_message_date }
                            inboxAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_READ_ALL_MESSAGES_LOCALLY)) {
                val inboxItem: InboxItem? = it.getParcelable(BUS_EVENT_READ_ALL_MESSAGES_LOCALLY)

                val index = inboxAdapter.displayList.indexOfLast { inbox -> inbox.chat_id == inboxItem?.chat_id }
                if (index != -1) {
                    inboxAdapter.displayList[index].unread_count = 0
                    inboxAdapter.notifyItemChanged(index, InboxAdapter.PAYLOAD_INBOX_ITEM)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}