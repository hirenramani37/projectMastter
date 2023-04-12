package com.massttr.user.ui.main.myprofile.order_history.view_receipt

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.JobDetails
import com.massttr.user.R
import com.massttr.user.databinding.ActivityViewReceiptBinding
import com.massttr.user.ui.main.myprofile.order_history.view_receipt.view_receipt_successfully.ViewReceiptSuccessfullyActivity
import org.jetbrains.anko.startActivity


class ViewReceiptActivity :
    BaseActivity<ActivityViewReceiptBinding>(R.layout.activity_view_receipt), View.OnClickListener {
    private val viewModel: ViewReceiptActivityViewModel by viewModels()
    private lateinit var photosTaskAdapter: ViewReceiptAdapter
    private lateinit var photosCompletedTaskAdapter: ViewReceiptAdapter
    private var title: String = ""
    private var receiptLink: String = ""

    companion object {
        const val JOB_ID = "JOB_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@ViewReceiptActivity) { handleError(it) }
            appLoader.observe(this@ViewReceiptActivity) { updateLoaderUI(it) }
            jobId.observe(this@ViewReceiptActivity) {
                binding.run {
                    title = it.data?.job?.title.toString()
                    receiptLink = it.data?.job?.receipt_link.toString()
                    tvOrderName.text = title
                    tvOrderDescription.text = it.data?.job?.description
                    tvOrderPrice.text = "$".plus(it.data?.job?.price)
                    tvViewReceiptDate.text =
                        it.data?.job?.appointment_date.plus(",".plus(it.data?.job?.appointment_time))
                    it.data?.job?.job_image?.let { list ->
                        photosTaskAdapter.addAll(list.filter {
                            it.type == 0
                        })
                        photosCompletedTaskAdapter.addAll(list.filter {
                            it.type == 2
                        })
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.view_receipt)
            if (intent.hasExtra(JOB_ID))
                viewModel.jobId(JobDetails(intent.getIntExtra(JOB_ID, 0)))

            photosTaskAdapter = ViewReceiptAdapter()
            photosCompletedTaskAdapter = ViewReceiptAdapter()
            rvPhotoTask.adapter = photosTaskAdapter
            rvPhotoTaskCompleted.adapter = photosCompletedTaskAdapter
        }
    }

    private fun clickListener() {
        binding.run {
            btnDownloadReceipt.setOnClickListener(this@ViewReceiptActivity)
            toolBar.imgBack.setOnClickListener(this@ViewReceiptActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnDownloadReceipt -> {
                startDownloadService()
                startActivity<ViewReceiptSuccessfullyActivity>()
                finish()
            }
        }
    }

    private fun startDownloadService() {
        val request = DownloadManager.Request(Uri.parse(receiptLink))
        request.setTitle(title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
            "/User/$title.pdf")
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        request.allowScanningByMediaScanner()
        request.setMimeType("application/pdf")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        manager.enqueue(request)
    }
}