package com.massttr.user.ui.main.myprofile.order_history.order_details

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.common.base.BaseActivity
import com.common.data.network.model.JobImage
import com.common.data.network.model.request.CompleteTaskStatus
import com.common.data.network.model.request.JobDetails
import com.massttr.user.R
import com.massttr.user.databinding.ActivityOrderDetailsBinding
import com.massttr.user.ui.main.home.LocationActivity
import com.massttr.user.ui.main.inbox.chat.edit_price.EditTaskPriceActivity
import com.massttr.user.ui.main.myprofile.order_history.order_details.cancel_task.CancelTasksActivity
import com.massttr.user.ui.main.myprofile.order_history.order_details.complete_task.CompleteTaskActivity
import com.massttr.user.ui.main.myprofile.order_history.order_details.showImage.ShowImageActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class OrderDetailsActivity :
    BaseActivity<ActivityOrderDetailsBinding>(R.layout.activity_order_details),
    View.OnClickListener {

    private val viewModel: OrderDetailsViewModel by viewModels()
    private lateinit var orderPhotoAdapter: OrderDetailsJobPhotoAdapter
    private var jobId = 0
    private var fixerId = 0
    private var priceType = 0
    private var receiptLink = ""
    private var locationLatitude = ""
    private var locationLongitude = ""
    private var phoneNo = ""
    private lateinit var taskImage : List<JobImage>

    companion object {
        const val JOB_ID = "JOB_ID"
        const val REQ_CANCEL_JOB = 78
        const val REQ_EDIT_PRICE = 79
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@OrderDetailsActivity) { handleError(it) }
            appLoader.observe(this@OrderDetailsActivity) { updateLoaderUI(it) }
            jobDetails.observe(this@OrderDetailsActivity) {
                binding.viewRoot.visible()
                binding.run {
                    it.data?.job?.banner_image?.let { img ->
                        loadImages(img, civProfile, R.drawable.ic_placeholder)
                    }
                    receiptLink = it.data?.receipt_link.toString()
                    tvOrderPrice.text = (it.data?.job?.price).plus(" QD")
                    tvOrderName.text = it.data?.job?.title
                    tvOrderDescription.text = it.data?.job?.description
                    tvOrderDate.text =
                        it.data?.job?.appointment_date.plus(", ".plus(it.data?.job?.appointment_time))

                    if (it.data?.job?.job_image?.isEmpty() == true) {
                        tvTaskTitle.gone()
                    } else {

                        it.data?.job?.job_image?.let { list ->
                            orderPhotoAdapter.addAll(list)
                        }
                    }

                    it.data?.job?.let { id -> checkStatus(id.job_status_id, id.user_rating) }
                    //Fixer Profile
                    if (it.data?.job?.fixer != null) {
                        fixerView.visible()
                        it.data.job.fixer.profile_picture.let { img ->
                            loadImages(img, ivFixerProfile, R.drawable.ic_placeholder)
                        }
                        tvCompletedJobs.text =
                            it.data.job.fixer.total_completed_jobs.toString()
                                .plus(" Completed tasks")
                        tvFixerName.text = it.data.job.fixer.full_name
                        tvFixerRating.text = it.data.job.fixer.avg_rating.toString()
                    }
                    fixerId = it.data?.job?.fixer_id ?: 0
                    priceType = it.data?.job?.price_type ?: 0
                    locationLatitude = it.data?.job?.fixer?.location_latitude ?: ""
                    locationLongitude = it.data?.job?.fixer?.location_longitude ?: ""
                    phoneNo = it.data?.job?.fixer?.phone_no ?: ""
                    taskImage = it.data?.job?.job_image!!
                }

                //2=Accept, 3=Start, 4=End, 5=CompleteJob, 6=User Cancel
                val jobStatus = it.data?.job
                if (jobStatus?.job_status_id == 5) {
                    binding.viewBottom.isVisible = false
                } else if (jobStatus?.job_status_id == 2 || jobStatus?.job_status_id == 10) {
                    binding.tvCompleteTask.isVisible = false
                    binding.tvCancelTask.isVisible = true
                }
            }

            jobStatus.observe(this@OrderDetailsActivity) {
                val intent = Intent(this@OrderDetailsActivity, CompleteTaskActivity::class.java)
                intent.putExtra(CompleteTaskActivity.JOB_ID, jobId)
                intent.putExtra(CompleteTaskActivity.FIXER_ID, fixerId)
                intent.putExtra(CompleteTaskActivity.PHOTOS_OF_TASK, orderPhotoAdapter.displayList)
                startActivityForResult(intent, REQ_CANCEL_JOB)
                //CompleteTaskActivity.PHOTOS_OF_TASK to orderPhotoAdapter.displayList
            }
        }
    }

    private fun init() {
        binding.run {
            binding.toolBar.tvTitle.text = getString(R.string.task_details)
            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
            viewModel.jobDetails(JobDetails(jobId))

            orderPhotoAdapter = OrderDetailsJobPhotoAdapter(this@OrderDetailsActivity)
            rvJobPhotos.adapter = orderPhotoAdapter
        }
    }

    private fun checkStatus(statusId: Int, userRating: Int) {
        binding.run {
            if (statusId == 4 || statusId == 5) {  //End
                viewBottom.gone()
                tvOrderStatus.text = getText(R.string.completed)
                downloadView()
                if (userRating == 0) {
                    viewBottom.visible()
                    tvCancelTask.gone()
                    tvCompleteTask.visible()
                    tvCompleteTask.text = getString(R.string.give_a_review)
                }
            } else if (statusId == 3 || statusId == 6) {  //User Cancel
                viewBottom.gone()
                tvEditPrice.gone()
                tvOrderStatus.text = getText(R.string.cancel)
                tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorWhite
                    )
                )
                tvOrderStatus.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@OrderDetailsActivity,
                            R.color.colorRed
                        )
                    )
            } else { //Pending
                tvCancelTask.visible()
                tvCompleteTask.gone()
                tvOrderStatus.text = getText(R.string.pending)
                tvEditPrice.text = getText(R.string.edit_price)
                tvEditPrice.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorPrimaryDark
                    )
                )
                tvEditPrice.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@OrderDetailsActivity,
                            R.color.light_yellow
                        )
                    )
            }
        }
    }

    private fun downloadView() {
        binding.run {
            tvEditPrice.text = getText(R.string.download)
            tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@OrderDetailsActivity,
                    R.color.colorGreen
                )
            )
            tvOrderStatus.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.light_green
                    )
                )
            tvEditPrice.setTextColor(
                ContextCompat.getColor(
                    this@OrderDetailsActivity,
                    R.color.colorWhite
                )
            )
            tvEditPrice.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorPrimaryDark
                    )
                )
            tvEditPrice.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_download, 0, 0, 0
            )
        }
    }

    private fun clickListener() {
        binding.run {
            tvCompleteTask.setOnClickListener(this@OrderDetailsActivity)
            tvCancelTask.setOnClickListener(this@OrderDetailsActivity)
            tvEditPrice.setOnClickListener(this@OrderDetailsActivity)
            imgLocation.setOnClickListener(this@OrderDetailsActivity)
            imgCall.setOnClickListener(this@OrderDetailsActivity)
            toolBar.imgBack.setOnClickListener(this@OrderDetailsActivity)

            orderPhotoAdapter.setItemClickListener { view, _, _ ->
                when(view.id){
                    R.id.ivPhotoTask-> startActivity<ShowImageActivity>(
                        ShowImageActivity.IMAGE_LIST to orderPhotoAdapter.displayList
                    )
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.imgCall -> this.call(phoneNo)
            R.id.tvCompleteTask ->
                viewModel.jobStatus(CompleteTaskStatus(jobId.toString(), "5", fixerId.toString()))
            R.id.tvCancelTask -> {
                val intent = Intent(this, CancelTasksActivity::class.java)
                intent.putExtra(CancelTasksActivity.JOB_ID, jobId)
                startActivityForResult(intent, REQ_CANCEL_JOB)
            }
            R.id.tvEditPrice ->
                if (getString(R.string.download) == binding.tvEditPrice.text.toString())
                    AppGlobal.openPdfIntent(this, receiptLink)
                else {
                    val intent = Intent(this, EditTaskPriceActivity::class.java)
                    intent.putExtra(EditTaskPriceActivity.JOB_ID, jobId)
                    intent.putExtra(EditTaskPriceActivity.PRICE_TYPE, priceType)
                    startActivityForResult(intent, REQ_EDIT_PRICE)
                }
            R.id.imgLocation -> {
                startActivity<LocationActivity>(
                    LocationActivity.LAT to locationLatitude,
                    LocationActivity.LONG to locationLongitude
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_CANCEL_JOB -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                REQ_EDIT_PRICE -> setResult(Activity.RESULT_OK)
            }
        }
    }
}
