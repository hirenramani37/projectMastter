package com.massttr.provider.ui.main.availableTasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseFragment
import com.common.data.network.model.request.AvailableTask
import com.massttr.provider.R
import com.massttr.provider.databinding.FragmentAvailableTasksBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.ViewTaskAcceptActivity
import com.massttr.user.utils.AVAILABLE_FIXDETAIL
import com.massttr.user.utils.setAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import java.util.*
import java.util.Collections.reverse

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class AvailableTasksFragment :
    BaseFragment<FragmentAvailableTasksBinding>(R.layout.fragment_available_tasks) {

    private val viewModel: AvailableTaskViewModel by viewModels()
    private lateinit var availableTaskAdapter: AvailableTaskAdapter
    private var currentPage = 1
    private var lastPage: Int = 0

    // lateinit var data: MainData
    var isLoadMore: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setObserver()
        initAdapter()
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(requireActivity()) { handleError(it) }
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            availableTask.observe(requireActivity()) {
                //  it.data?.data?.let { list -> availableTaskAdapter.addAll(list) }

                if (isLoadMore) {
                    it.data?.data?.let { TaskList ->
                       // reverse(TaskList)
                        availableTaskAdapter.appendAll(TaskList)
                    }
                    currentPage++
                    availableTaskAdapter.notifyDataSetChanged()
                } else {
                    lastPage = it.data?.last_page ?: 0
                    currentPage++
                    it.data?.data?.let { TaskList ->
                       // reverse(TaskList)
                        availableTaskAdapter.addAll(TaskList)
                    }
                }
                isLoadMore = currentPage <= lastPage

            }
        }
    }

    private fun initAdapter() {
        binding.rvFixerTask.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == availableTaskAdapter.itemCount - 1) {
                    if (isLoadMore) {
                        availableJobs()
                    }
                }
            }
        })
    }

    private fun availableJobs() {
        viewModel.availableTask(AvailableTask(pref.kmProgress?:0), currentPage)
    }


    private fun setUpUI() {

        binding.run {
//            tvKm.text = getString(R.string.km, pref.kmProgress.toString()))
            tvKm.text = (pref.kmProgress.toString()+getString(R.string.km_))
            availableTaskAdapter = AvailableTaskAdapter(requireContext())
            // rvFixerTask.adapter = availableTaskAdapter
            rvFixerTask.setAdapter(availableTaskAdapter, binding.llNoTask)
            availableJobs()
            availableTaskAdapter.setItemClickListener { view, _, item ->
                when (view.id) {
                    R.id.clMain ->
                        requireActivity().startActivity<ViewTaskAcceptActivity>(
                            AVAILABLE_FIXDETAIL to item.id.toInt()
                        )
                }
            }
        }
    }
}


