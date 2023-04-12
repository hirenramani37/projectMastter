package com.massttr.user.ui.main.inbox

import android.os.Bundle
import android.view.View
import com.common.base.BaseFragment
import com.massttr.user.utils.*
import com.massttr.user.R
import com.massttr.user.chat.*
import com.massttr.user.databinding.FragmentInboxBinding
import com.massttr.user.ui.main.inbox.chat.ChatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class InboxFragment : BaseFragment<FragmentInboxBinding>(R.layout.fragment_inbox) {
    private lateinit var inboxAdapter: InboxAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setUpUI()
        clickListener()
        initBusEvent()
    }

    private fun init() {
        inboxAdapter = InboxAdapter()
        binding.rvInbox.setAdapter(inboxAdapter, binding.viewNoFound)

        SocketService.getInstance()?.sendListChats(0)
    }

    private fun setUpUI() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.inbox)
        }
    }

    private fun clickListener() {
        inboxAdapter.setItemClickListener { view, _, inbox ->
            when (view.id) {
                R.id.clMain -> {
                    requireContext().startActivity<ChatActivity>(ChatActivity.INBOX to inbox)
                }
            }
        }
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey(BUS_EVENT_INBOX_LIST)) {
                val inboxList: ArrayList<Inbox>? = it.getParcelableArrayList(BUS_EVENT_INBOX_LIST)
                Timber.d("initBusEvent : size: ${inboxList?.size}")
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
                        }else{
                            inboxAdapter.displayList.sortByDescending { it.last_message_date }
                            inboxAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_CHAT_COUNT)) {
                val readCountChat = it.getInt(BUS_EVENT_CHAT_COUNT, 0)
                pref.chatCount = readCountChat
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
}
