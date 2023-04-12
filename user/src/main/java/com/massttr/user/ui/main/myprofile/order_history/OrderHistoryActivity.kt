package com.massttr.user.ui.main.myprofile.order_history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityOrderHistoryBinding
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.FixerListActivity
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.FixerProfileActivity
import com.massttr.user.ui.main.inbox.chat.edit_price.EditTaskPriceActivity
import com.massttr.user.ui.main.myprofile.order_history.order_details.OrderDetailsActivity
import com.massttr.user.ui.main.myprofile.order_history.view_receipt.ViewReceiptActivity
import com.massttr.user.ui.main.myprofile.order_history.view_receipt.ViewReceiptActivity.Companion.JOB_ID
import com.massttr.user.utils.setAdapter
import com.massttr.user.utils.visible
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class OrderHistoryActivity :
    BaseActivity<ActivityOrderHistoryBinding>(R.layout.activity_order_history),
    View.OnClickListener {
    private val viewModel: OrderHistoryViewModel by viewModels()
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private var currentPage = 1
    private var lastPage: Int = 0
    private var changePricePosition = -1
    private var isLoadMore: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpObserver()
        setUpUI()
        clickListener()
        initAdapter()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@OrderHistoryActivity) { handleError(it) }
            appLoader.observe(this@OrderHistoryActivity) { updateLoaderUI(it) }
            orderHistory.observe(this@OrderHistoryActivity) {
                if (isLoadMore) {
                    it.data?.let { it1 -> orderHistoryAdapter.appendAll(it1) }
                    currentPage++
                    orderHistoryAdapter.notifyDataSetChanged()
                } else {
                    lastPage = it?.last_page ?: 0
                    currentPage++
                    it.data?.let { it1 -> orderHistoryAdapter.addAll(it1) }
                }
                isLoadMore = currentPage <= lastPage

                if (orderHistoryAdapter.displayList.size == 0) {
                    binding.viewNoFound.visible()
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvOrderHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderHistoryAdapter.itemCount - 1) {
                    if (isLoadMore) orderHistory()
                }
            }
        })
    }

    private fun orderHistory() {
        viewModel.orderHistory(currentPage)
    }

    private fun setUpUI() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.order_history)
            orderHistory()
            orderHistoryAdapter = OrderHistoryAdapter(this@OrderHistoryActivity)
            rvOrderHistory.setAdapter(orderHistoryAdapter, viewNoFound)
        }
    }


    private fun clickListener() {
        binding.toolBar.imgBack.setOnClickListener(this)
        orderHistoryAdapter.setItemClickListener { view, position, item ->
            when (view.id) {
                R.id.tvDownload -> startActivity<ViewReceiptActivity>(JOB_ID to item.id)
                R.id.clMain -> {
                    val intent =
                        Intent(this@OrderHistoryActivity, OrderDetailsActivity::class.java)
                    intent.putExtra(OrderDetailsActivity.JOB_ID, item.id)
                    //startActivityForResult(intent, REQ_CANCEL_JOB)
                    resultCancel.launch(intent)
                }
                R.id.tvViewFixer -> { // //2=Accept, 3=Start, 4=End, 5=CompleteJob, 6=User Cancel
                    if (item.job_status_id == 2 || item.job_status_id == 3) {
                        startActivity<FixerProfileActivity>(
                            FixerProfileActivity.JOB_ID to item.id,
                            FixerProfileActivity.FIXER_ID to item.fixer_id,
                            FixerProfileActivity.JOB_STATUS_ID to item.job_status_id
                        )
                    } else if (item.job_status_id == 4 || item.job_status_id == 5) {
                        val intent =
                            Intent(this@OrderHistoryActivity, OrderDetailsActivity::class.java)
                        intent.putExtra(OrderDetailsActivity.JOB_ID, item.id)
                        //startActivityForResult(intent, REQ_CANCEL_JOB)
                    } else if (item.job_status_id == 6) {

                    } else {
                        startActivity<FixerListActivity>(FixerListActivity.JOB_ID to item.id)
                    }
                }
                R.id.tvEditPrice -> {
                    changePricePosition = position
                    val intent = Intent(this, EditTaskPriceActivity::class.java)
                    intent.putExtra(EditTaskPriceActivity.JOB_ID, item.id)
                    intent.putExtra(EditTaskPriceActivity.PRICE, item.price)
                    intent.putExtra(EditTaskPriceActivity.PRICE_TYPE, item.price_type)
                    resultEditPrice.launch(intent)
                }
            }
        }
    }

    private var resultEditPrice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val price = data?.getStringExtra(EditTaskPriceActivity.PRICE).orEmpty()
                if (orderHistoryAdapter.displayList[changePricePosition].price_type == 0) {
                    orderHistoryAdapter.displayList[changePricePosition].price = price
                    orderHistoryAdapter.notifyItemChanged(changePricePosition)
                } else {
                    orderHistoryAdapter.displayList[changePricePosition].price = price
                    //orderHistoryAdapter.displayList[changePricePosition].approx_hour = hour.toInt()
                    orderHistoryAdapter.notifyItemChanged(changePricePosition)
                }
            }
        }

    private var resultCancel =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) finish()
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}