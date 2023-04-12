package com.massttr.provider.ui.main.notification

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationManagerCompat
import com.common.data.network.model.CompleteJobMainData
import com.massttr.user.utils.BaseUtils
import com.massttr.user.utils.EXTRA_RECEIPT_DETAIL
import com.massttr.user.utils.getNameFromUrl
import com.massttr.user.utils.getSaveDir
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock

import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import timber.log.Timber
import java.io.File


class DownloadInvoiceService : IntentService("Service"), FetchListener {
    private var fetch: Fetch? = null
    private var request: Request? = null
    var notification_Id = 1
    private var handler: Handler? = null
    private var channel: NotificationChannel? = null
    var currentProgress: Int = 0
    private var completedJob: CompleteJobMainData? = null

    companion object {
        private var builder: Notification.Builder? = null
        private var notificationManagerCompat: NotificationManagerCompat? = null
        val PROGRESS_UPDATE = "PROGRESS_UPDATE"
    }

    override fun onCreate() {
        super.onCreate()
        //handler = Handler()
        handler = Handler(Looper.getMainLooper())
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_RECEIPT_DETAIL)) {
                completedJob = intent.getSerializableExtra(EXTRA_RECEIPT_DETAIL) as CompleteJobMainData?
                downloadInvoice()
            }
        }
    }

    private fun showNotification(
        id: Int,
        totalLength: Int,
        title: String?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel =
                NotificationChannel(
                    "1",
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel?.description = "This is Description"
            val manager =
                BaseUtils.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel!!)
            builder =
                Notification.Builder(BaseUtils.context, "1")
        } else {
            builder = Notification.Builder(BaseUtils.context)
        }
        builder?.setContentTitle(title)
        builder?.setAutoCancel(false)
        builder?.setProgress(totalLength, 0, false)
        builder?.setWhen(System.currentTimeMillis())
        builder?.setOnlyAlertOnce(true)
//        builder?.setOngoing(true)
        builder?.setPriority(Notification.PRIORITY_LOW)
        builder?.setSmallIcon(R.mipmap.ic_launcher_foreground)
        notificationManagerCompat =
            NotificationManagerCompat.from(BaseUtils.context)
        builder?.build()
            ?.let { notificationManagerCompat?.notify(id, it) }
    }

    private fun sendProgressUpdate() {
        val intent = Intent(PROGRESS_UPDATE)
        intent.putExtra("downloadComplete", true)
        Timber.e("Download Finish")
        sendBroadcast(intent)
    }


    private fun downloadInvoice() {
        fetch = Fetch.getInstance(MyApp.getInstance().getFetchConfiguration())
        handler?.postDelayed({
            val filePath = getSaveDir() + "/${completedJob?.title}.pdf"
            val file = File(filePath)
            request = completedJob?.receipt_link?.let { Request(it, filePath) }
            if (!file.exists()) {
                if (request != null) {
                    fetch?.addListener(this@DownloadInvoiceService)
                        ?.enqueue(request!!, null)
                }
            } else {
                toast("File already downloaded at $filePath")
            }
        }, 1000)
    }

    override fun onAdded(download: Download) {
        showNotification(download.id, download.file.length, getNameFromUrl(download.file))
        Timber.e("DOWNLOAD_ADDED")
    }

    override fun onCancelled(download: Download) {

    }

    override fun onCompleted(download: Download) {
        builder?.setContentText("Download Finished")
        builder?.setProgress(0, 0, false)
//        builder?.setOngoing(false)
        builder?.build()
            ?.let { notificationManagerCompat?.notify(download.id, it) }
        Timber.e("Download_Finish")
//        sendProgressUpdate()
//        BaseUtils.context.startActivity<SuccessActivity>(SuccessActivity.SUCCESSIVE_TYPE to SuccessiveType.VIEW_RECEIPT_TYPE.type)
    }

    override fun onDeleted(download: Download) {
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {

    }

    override fun onError(
        download: Download,
        error: com.tonyodev.fetch2.Error,
        throwable: Throwable?
    ) {

    }



    override fun onPaused(download: Download) {

    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Timber.e("DOWNLOAD_PROGRESS_CHANGED")
        Timber.e("File_Size ${download.file.length.toLong()}")
        runOnUiThread {
            var incr: Int = 0
            while (incr < download.file.length) {
                Timber.e("Progress ${etaInMilliSeconds.toInt()},${download.total.toInt()}, ${download.downloaded.toInt()}")
                builder?.setProgress(download.file.length, incr, false)
                //Displays the progress bar for the first time.
                builder?.build()
                    ?.let { notificationManagerCompat?.notify(download.id, it) }

                // Sleeps the thread, simulating an operation
                try {
                    // Sleep for 1 second
                    Thread.sleep(1 * 1000.toLong())
                } catch (e: InterruptedException) {
                    Timber.e("sleep failure")
                }
//            if (incr == download.file.length)
                incr += 5
            }
        }

    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

    }

    override fun onRemoved(download: Download) {

    }

    override fun onResumed(download: Download) {

    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {

    }

    override fun onWaitingNetwork(download: Download) {

    }
}
