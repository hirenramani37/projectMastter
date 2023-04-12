package com.massttr.user.ui.main.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.app.common.utils.RVEmptyObserver
import com.common.base.BaseFragment
import com.common.stickyheader.StickyHeaderDecoration
import com.massttr.user.R
import com.massttr.user.chat.SocketService
import com.massttr.user.databinding.FragmentNotificationBinding
import com.massttr.user.ui.main.myprofile.order_history.order_details.OrderDetailsActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class NotificationFragment :
    BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification){

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setUpObserver()
        SocketService.getInstance()?.checkNotificationCount()
        onClickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            apiErrors.observe(requireActivity()) { handleError(it) }
            notification.observe(requireActivity()) {
                it.data?.let { list -> notificationAdapter.addAll(list) }
            }
        }
    }

    private fun initView() {
        SocketService.getInstance()?.readNotification()
        binding.run {
            toolbar.tvTitle.text = getString(R.string.notification)
            viewModel.notificationList()
            notificationAdapter = NotificationAdapter(requireContext().applicationContext)
            rvNotification.addItemDecoration(
                StickyHeaderDecoration(
                    notificationAdapter,
                    false,
                    true
                )
            )
            notificationAdapter.registerAdapterDataObserver(
                RVEmptyObserver(
                    viewNoFound,
                    rvNotification
                )
            )
            rvNotification.adapter = notificationAdapter
        }
    }

    private fun onClickListener() {
        notificationAdapter.setItemClickListener { _, _, notificationResponse ->
            requireActivity().startActivity<OrderDetailsActivity>(OrderDetailsActivity.JOB_ID to notificationResponse.job_id)
        }
    }
}
