package com.massttr.provider.ui.main.myprofiles.completedTasks.viewReceipt

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.CompleteJobMainData
import com.common.data.network.model.request.JobImage
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityViewReceiptBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.showImage.ShowImageActivity
import com.massttr.provider.ui.main.myprofiles.completedTasks.CompletedTaskViewModel
import com.massttr.provider.ui.main.myprofiles.completedTasks.viewReceipt.receiptSuccess.ViewReceiptSuccessActivity
import com.massttr.provider.ui.main.notification.DownloadInvoiceService.Companion.PROGRESS_UPDATE
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

class ViewReceiptActivity :
    BaseActivity<ActivityViewReceiptBinding>(R.layout.activity_view_receipt), View.OnClickListener {

    private val viewModel: CompletedTaskViewModel by viewModels()
    private lateinit var adapterBeforeJobPhoto: PhotosTasksAdapter
    private lateinit var adapterAfterJobPhoto: PhotosTasksAdapter
    private var completedJob: CompleteJobMainData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
        setObserver()
    }

    private fun initializeBroacasteReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(PROGRESS_UPDATE)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun initView() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.view_receipt)
            adapterBeforeJobPhoto = PhotosTasksAdapter()
            adapterAfterJobPhoto = PhotosTasksAdapter()
            rvPhotoTask.adapter = adapterBeforeJobPhoto
            rvPhotoTaskCompleted.adapter = adapterAfterJobPhoto
        }

        if (intent.hasExtra(EXTRA_RECEIPT_DETAIL)) {
            completedJob = intent.getSerializableExtra(EXTRA_RECEIPT_DETAIL) as CompleteJobMainData?
            binding.run {
                tvOrderName.text = completedJob?.user?.full_name
                tvOrderDescription.text = completedJob?.description
                tvViewReceiptDate.text = completedJob?.completed_task_datetime
//                tvViewReceiptDate.text =
//                    completedJob?.appointment_date.plus(" " + completedJob?.appointment_time)
                tvOrderPrice.text = completedJob?.price?.plus(" QD")
            }
        }

        viewModel.run {
            getAfterJobImage(
                JobImage(
                    completedJob?.id ?: 0,
                    2
                )
            )
            getBeforeJobImage(
                JobImage(
                    completedJob?.id ?: 0,
                    1
                )
            )
        }
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@ViewReceiptActivity) { handleError(it) }
            appLoader.observe(this@ViewReceiptActivity) { updateLoaderUI(it) }
            beforeImage.observe(this@ViewReceiptActivity) {
                binding.run {
                    if (it.isEmpty()) {
                        tvPhotosTask.gone()
                        rvPhotoTask.gone()
                    } else {
                        tvPhotosTask.visible()
                        rvPhotoTask.visible()
                        adapterBeforeJobPhoto.addAll(it)
                    }
                }
            }
            afterImage.observe(this@ViewReceiptActivity) {
                binding.run {
                    if (it.isEmpty()) {
                        rvPhotoTaskCompleted.gone()
                        tvPhotosTaskComplete.gone()
                    } else {
                        rvPhotoTaskCompleted.visible()
                        tvPhotosTaskComplete.visible()
                        adapterAfterJobPhoto.addAll(it)
                    }
                }
            }
        }
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    private fun clickListener() {
        binding.run {
            btnDownloadReceipt.setOnClickListener(this@ViewReceiptActivity)
            toolBar.imgBack.setOnClickListener(this@ViewReceiptActivity)
            adapterBeforeJobPhoto.setItemClickListener { view, i, jobImage ->
                when (view.id) {
                    R.id.ivPhotosTasks ->
                        startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to jobImage.image)
                }
            }

            adapterAfterJobPhoto.setItemClickListener { view, i, jobImage ->
                when (view.id) {
                    R.id.ivPhotosTasks ->
                        startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to jobImage.image)
                }
            }
        }
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(mContext: Context?, intent: Intent?) {
            if (intent?.action == PROGRESS_UPDATE) {
                val isDownload = intent.getBooleanExtra("downloadComplete", false)
                if (isDownload) {
                    startActivity<ViewReceiptSuccessActivity>()
                    //startActivity<SuccessActivity>(SuccessActivity.SUCCESSIVE_TYPE to SuccessiveType.VIEW_RECEIPT_TYPE.type)
                }
            }
        }
    }

    private fun startDownloadService() {
        val request = DownloadManager.Request(Uri.parse(completedJob?.receipt_link))
        request.setTitle(completedJob?.title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "/Fixer/" + completedJob?.title + ".pdf"
        )
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        request.allowScanningByMediaScanner()
        request.setMimeType("application/pdf")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        manager.enqueue(request)
    }

    private fun startDownload() {
        val request = DownloadManager.Request(Uri.parse("receiptLink"))
        request.setTitle(title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "/User/$title.pdf"
        )
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        request.allowScanningByMediaScanner()
        request.setMimeType("application/pdf")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        manager.enqueue(request)
    }

    private fun requestPermission() {
        permissionUtils.requestPermissions(
            PermissionUtils.REQUEST_CAMERA_GALLERY_PERMISSION,
            PermissionUtils.REQUEST_CODE_CAMERA_GALLERY_PERMISSION
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnDownloadReceipt -> {
                /*if (!permissionUtils.checkPermission(PermissionUtils.REQUEST_CAMERA_GALLERY_PERMISSION)) {
                    requestPermission()
                } else {
                    startDownloadService()
                }*/
                startDownloadService()
                startActivity<ViewReceiptSuccessActivity>()
                finish()
            }
        }
    }
}
