package com.massttr.provider.ui.main.myUpcomingFix

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseFragment
import com.massttr.user.utils.*
import com.massttr.provider.R
import com.massttr.provider.chat.Inbox
import com.massttr.provider.chat.FixerSocketService
import com.massttr.provider.databinding.FragmentSettingsBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.ViewTaskAcceptActivity
import com.massttr.provider.ui.main.myUpcomingFix.cancelTask.CancelTasksActivity
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.StartTasksActivity
import com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat.ChatActivity
import com.massttr.provider.ui.main.myUpcomingFix.endTask.TaskEndActivity
import com.massttr.provider.ui.main.myUpcomingFix.endTask.TaskEndActivity.Companion.TASK_ID
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class MyUpcomingFixFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {

    private lateinit var settingsAdapter: MyUpcomingFixAdapter
    private val viewModel: MyUpcomingFixViewModel by viewModels()
    private var currentPage = 1
    private var lastPage: Int = 0
    var isLoadMore: Boolean = false

    private var isChatCreate: Boolean = false

    companion object {
        const val REQ_CANCEL_JOB = 77
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAdapter()
        setObserver()
        initBusEvent()
    }

    private fun setObserver() {
        viewModel.run {
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            apiErrors.observe(requireActivity()) { handleError(it) }
            fixList.observe(requireActivity()) { myfix ->
                if (myfix != null) {
                    myfix.data?.let {
                        if (isLoadMore) {
                            settingsAdapter.appendAll(it)
                            currentPage++
                            settingsAdapter.notifyDataSetChanged()
                        } else {
                            lastPage = myfix.last_page ?: 0
                            currentPage++
                            settingsAdapter.addAll(it)
                        }
                        isLoadMore = currentPage <= lastPage
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvMyUpComingFix.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == settingsAdapter.itemCount - 1) {
                    if (isLoadMore) myUpcomingFixer()
                }
            }
        })
    }

    private fun myUpcomingFixer(){
        viewModel.myFixList(currentPage)
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.my_upcoming_fix)
            settingsAdapter = MyUpcomingFixAdapter(requireContext())
            rvMyUpComingFix.setAdapter(settingsAdapter, llNoTask)
            settingsAdapter.setItemClickListener { view, _, myFix ->
                when (view.id) {
                    R.id.btnStartTask -> requireActivity().startActivity<StartTasksActivity>(
                        START_JOB_ACTIVITY to myFix.id
                    )
                    R.id.viewRoot -> requireActivity().startActivity<ViewTaskAcceptActivity>(
                        AVAILABLE_FIXDETAIL to myFix.id
                    )
                    R.id.btnCancelTask -> {
                        val intent = Intent(context, CancelTasksActivity::class.java)
                        intent.putExtra(JOB_CANCEL_ACTIVITY, myFix.id)
                        startActivityForResult(intent, REQ_CANCEL_JOB)
                    }
                    R.id.btnChat -> {
                        if (myFix.user_id != null && myFix.id != null) {
                            isChatCreate = true
                            updateLoaderUI(true)
                            FixerSocketService.getInstance()?.sendCreateChat(myFix.user_id!!, myFix.id!!)
                        }
                    }
                    R.id.btnEndTask -> {
                        requireActivity().startActivity<TaskEndActivity>(TASK_ID to myFix.id)

//                     val intent = Intent(context, TaskEndActivity::class.java)
//                        intent.putExtra(TASK_ID, myFix.id)
//                        startActivityForResult(intent, REQ_JOB_END)
//                        requireActivity().startActivity<TaskEndActivity>()
                    }
                }
            }
        }
        myUpcomingFixer()
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            Timber.d("Bundle : $it")
            if (it.containsKey(BUS_EVENT_CREATE_CHAT) && isChatCreate) {
                updateLoaderUI(false)
                val inbox = it.getParcelable(BUS_EVENT_CREATE_CHAT) as Inbox?
                inbox?.let {
                    isChatCreate = false
                    requireContext().startActivity<ChatActivity>(ChatActivity.INBOX to inbox)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_CANCEL_JOB -> {
//                    myUpcomingFixer()
                    val bundle = Bundle()
                    bundle.putBoolean("END_TASK", true)
                    EventBus.publish(bundle)

                }
            }
        }
    }
}
