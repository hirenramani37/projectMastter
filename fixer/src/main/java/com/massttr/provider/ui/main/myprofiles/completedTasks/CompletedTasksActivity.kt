package com.massttr.provider.ui.main.myprofiles.completedTasks

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.common.data.network.model.request.CompletedJob
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityCompletedTasksBinding
import com.massttr.provider.ui.main.myprofiles.completedTasks.viewReceipt.ViewReceiptActivity
import com.massttr.user.utils.EXTRA_RECEIPT_DETAIL
import com.massttr.user.utils.setAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class CompletedTasksActivity :
    BaseActivity<ActivityCompletedTasksBinding>(R.layout.activity_completed_tasks),
    View.OnClickListener {
    private lateinit var completedTasksAdapter: CompletedTasksAdapter
    private val viewModel: CompletedTaskViewModel by viewModels()
    private var currentPage = 1
    private var lastPage: Int = 0
    private var isLoadMore: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        setObserver()
        initAdapter()
        clickListener()
    }

    private fun setObserver() {
        viewModel.run {
            appLoader.observe(this@CompletedTasksActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@CompletedTasksActivity) { handleError(it) }
            job.observe(this@CompletedTasksActivity) {
                if (it?.jobs?.data != null) {
                    if (isLoadMore) {
                        it.jobs.data.let { list -> completedTasksAdapter.appendAll(list) }
                        currentPage++
                        completedTasksAdapter.notifyDataSetChanged()
                    } else {
                        lastPage = it.jobs.last_page ?: 0
                        currentPage++
                        it.jobs.data.let { list -> completedTasksAdapter.addAll(list) }
                    }
                    isLoadMore = currentPage <= lastPage
                }
            }
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this@CompletedTasksActivity)
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.complete_task)
            completedTasksAdapter = CompletedTasksAdapter()
            rvCompletedTasks.setAdapter(completedTasksAdapter, binding.tvNoTask)
            rvCompletedTasks.adapter = completedTasksAdapter
            completedTasksAdapter.setItemClickListener { view, position, _ ->
                when (view.id) {
                    R.id.btnDownloadReceipt -> {
                        val intent =
                            Intent(this@CompletedTasksActivity, ViewReceiptActivity::class.java)
                        intent.putExtra(
                            EXTRA_RECEIPT_DETAIL,
                            completedTasksAdapter.displayList[position]
                        )
                        startActivity(intent)
                    }
                }
            }
        }
        completedTask()
    }

    private fun initAdapter() {
        binding.rvCompletedTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == completedTasksAdapter.itemCount - 1) {
                    if (isLoadMore)
                        completedTask()
                }
            }
        })
    }

    private fun completedTask(){
        viewModel.completedTask(currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}
