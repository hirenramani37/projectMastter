package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.common.data.network.model.request.NearBrFixer
import com.common.data.network.model.request.NearBrFixerPopUp
import com.massttr.user.R
import com.massttr.user.chat.Inbox
import com.massttr.user.chat.SocketService
import com.massttr.user.databinding.DialogFixerFilterBinding
import com.massttr.user.databinding.FixerListActivityBinding
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.FixerProfileActivity
import com.massttr.user.ui.main.inbox.chat.ChatActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class FixerListActivity : BaseActivity<FixerListActivityBinding>(R.layout.fixer_list_activity),
    View.OnClickListener {

    private val viewModel: FixerListActivityViewModel by viewModels()
    private var fixerListAdapter: FixerListAdapter? = null
    private var jobId = 0
    private var ratingId = 0
    private var currentPage = 1
    private var lastPage: Int = 0
    private var isLoadMore: Boolean = false
    private var isChatCreate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpObserver()
        initBusEvent()
        initAdapter()
        clickListener()
    }

    companion object {
        const val JOB_ID = "JOB_ID"
        const val TASK_PHOTO_LIST = "TASK_PHOTO_LIST"
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@FixerListActivity) { handleError(it) }
            appLoader.observe(this@FixerListActivity) { updateLoaderUI(it) }
            nearByFixer.observe(this@FixerListActivity) {
                it.data.let { data ->
                    if (isLoadMore) {
                        if (data != null) {
                            fixerListAdapter?.appendAll(data)
                        }
                        currentPage++
                        fixerListAdapter?.notifyDataSetChanged()
                    } else {
                        lastPage = it?.last_page ?: 0
                        currentPage++
                        if (data != null) {
                            fixerListAdapter?.addAll(data)
                        }
                    }
                    isLoadMore = currentPage <= lastPage
                    //fixerListAdapter?.addAll(data)
                    binding.tvFixerCount.text = fixerListAdapter?.displayList?.size.toString()
                }
            }

            nearByFixerPopUp.observe(this@FixerListActivity) {
                fixerListAdapter?.clearAll()
                if (it.data != null) {
                    it.data.let { data ->
                        fixerListAdapter?.addAll(data)
                        binding.tvFixerCount.text = data.size.toString()
                    }
                }

            }
        }
    }

    private fun initAdapter() {
        binding.rvAvailableFixer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == fixerListAdapter!!.itemCount - 1) {
                    if (isLoadMore) getNearFixer()
                }
            }
        })
    }

    private fun getNearFixer() {
        viewModel.nearByFixer(NearBrFixer(jobId, currentPage))
    }

    private fun init() {
        binding.run {
            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
            /*if (intent.hasExtra("TASK_PHOTO_LIST"))
                fixerTaskPhotoAdapter?.addAll(intent.getSerializableExtra("TASK_PHOTO_LIST") as ArrayList<SelectPhotos>)*/
            fixerListAdapter = FixerListAdapter()
            rvAvailableFixer.setAdapter(fixerListAdapter, binding.viewNoFound)
            getNearFixer()

            rvAvailableFixer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && ivFilter.visibility == View.VISIBLE)
                        ivFilter.gone()
                    else if (dy < 0 && ivFilter.visibility != View.VISIBLE)
                        ivFilter.visible()
                }
            })
        }
    }

    private fun clickListener() {
        binding.run {
            ivFilter.setOnClickListener(this@FixerListActivity)
            imgBack.setOnClickListener(this@FixerListActivity)
            fixerListAdapter?.setItemClickListener { view, _, nearByFixersData ->
                when (view.id) {
                    R.id.btnChat -> {
                        isChatCreate = true
                        updateLoaderUI(true)
                        SocketService.getInstance()
                            ?.sendCreateChat(nearByFixersData.fixer_id, nearByFixersData.job_id)
                    }
                    R.id.mainView -> {
                        startActivity<FixerProfileActivity>(
                            FixerProfileActivity.FIXER_ID to nearByFixersData.fixer_id,
                            FixerProfileActivity.JOB_ID to nearByFixersData.job_id
                        )
                    }
                    R.id.imgBack -> onBackPressed()
                }
            }
        }
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            Timber.d("Bundle : $it")
            if (it.containsKey(BUS_EVENT_CREATE_CHAT) && isChatCreate) {
                updateLoaderUI(false)
                val inbox = it.getParcelable(BUS_EVENT_CREATE_CHAT) as Inbox?
                inbox?.let {
                    isChatCreate = false
                    startActivity<ChatActivity>(ChatActivity.INBOX to inbox)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.ivFilter -> filterDialog()
        }
    }

    private fun filterDialog() {
        val dialog = Dialog(this@FixerListActivity)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val binding: DialogFixerFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this@FixerListActivity),
            R.layout.dialog_fixer_filter,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.run {
            rat1.setOnClickListener {
                ratingId = 1
                rat1.setImageResource(R.drawable.ic_star)
                rat2.setImageResource(R.drawable.ic_star_gray)
                rat3.setImageResource(R.drawable.ic_star_gray)
                rat4.setImageResource(R.drawable.ic_star_gray)
                rat5.setImageResource(R.drawable.ic_star_gray)
            }
            rat2.setOnClickListener {
                ratingId = 2
                rat1.setImageResource(R.drawable.ic_star)
                rat2.setImageResource(R.drawable.ic_star)
                rat3.setImageResource(R.drawable.ic_star_gray)
                rat4.setImageResource(R.drawable.ic_star_gray)
                rat5.setImageResource(R.drawable.ic_star_gray)
            }
            rat3.setOnClickListener {
                ratingId = 3
                rat1.setImageResource(R.drawable.ic_star)
                rat2.setImageResource(R.drawable.ic_star)
                rat3.setImageResource(R.drawable.ic_star)
                rat4.setImageResource(R.drawable.ic_star_gray)
                rat5.setImageResource(R.drawable.ic_star_gray)
            }
            rat4.setOnClickListener {
                ratingId = 4
                rat1.setImageResource(R.drawable.ic_star)
                rat2.setImageResource(R.drawable.ic_star)
                rat3.setImageResource(R.drawable.ic_star)
                rat4.setImageResource(R.drawable.ic_star)
                rat5.setImageResource(R.drawable.ic_star_gray)
            }
            rat5.setOnClickListener {
                ratingId = 5
                rat1.setImageResource(R.drawable.ic_star)
                rat2.setImageResource(R.drawable.ic_star)
                rat3.setImageResource(R.drawable.ic_star)
                rat4.setImageResource(R.drawable.ic_star)
                rat5.setImageResource(R.drawable.ic_star)
            }

            tvClose.setOnClickListener { dialog.dismiss() }
            btnFilterApply.setOnClickListener {
                fixerListAdapter?.clearAll()
                viewModel.nearByFixerPopUp(NearBrFixerPopUp(jobId, ratingId))
                dialog.dismiss()
            }
            btnClear.setOnClickListener { /*toast("Clear")*/ }
        }
        dialog.setContentView(binding.root)
        /*dialog.setCancelable(false)*/
        dialog.show()
    }
}
