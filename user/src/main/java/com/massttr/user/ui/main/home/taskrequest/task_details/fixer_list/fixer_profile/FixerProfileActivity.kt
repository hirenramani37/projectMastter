package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.PastFixes
import com.common.data.network.model.request.BookFixer
import com.common.data.network.model.request.FixerProfile
import com.massttr.user.R
import com.massttr.user.chat.Inbox
import com.massttr.user.chat.SocketService
import com.massttr.user.databinding.ActivityProfileActivityBinding
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer.OrderAcceptedActivity
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.photoTask.PhotosTaskActivity
import com.massttr.user.ui.main.inbox.chat.ChatActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import timber.log.Timber

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class FixerProfileActivity :
    BaseActivity<ActivityProfileActivityBinding>(R.layout.activity_profile_activity),
    View.OnClickListener {
    private val viewModel: FixerProfileActivityViewModel by viewModels()
    private var fixerTasksPhotoAdapter: FixerTasksPhotoAdapter? = null
    private var documentAdapter: DocumentAdapter? = null
    private var jobId = 0
    private var fixerId = 0
    private var resumeData = ""
    private lateinit var photoOfTheTask: List<PastFixes>
    private var isChatCreate: Boolean = false

    companion object {
        const val JOB_ID = "JOB_ID"
        const val FIXER_ID = "FIXER_ID"
        const val JOB_STATUS_ID = "JOB_STATUS_ID"
        const val REQ_ORDER_CODE_LOST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUI()
        setUpObserver()
        clickListener()
        initBusEvent()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@FixerProfileActivity) { handleError(it) }
            appLoader.observe(this@FixerProfileActivity) { updateLoaderUI(it) }
            fixerProfile.observe(this@FixerProfileActivity) {
                binding.viewRoot.visible()
                binding.run {
                    it.data?.fixer?.profile_picture?.let { it1 ->
                        loadImages(
                            it1,
                            civProfile,
                            R.drawable.ic_placeholder
                        )
                    }
                    tvFixerRating.text = it?.data?.fixer?.avg_rating.toString()
                    tvFixerName.text = it?.data?.fixer?.full_name
                    if (it.data?.fixer?.nearbyfixer?.distance == null)
                        tvDistance.text = "0.00 km"
                    else
                        tvDistance.text = it.data.fixer.nearbyfixer.distance.plus(" km")
                    if (it.data?.fixer?.past_fixes?.isEmpty() == true) {
                        tvTask.gone()
                        tvViewAll.gone()
                        viewDivider1.gone()
                    } else {
                        it.data?.fixer?.past_fixes?.let { pastFixes ->
                            fixerTasksPhotoAdapter?.addAll(pastFixes)
                        }
                    }
                    photoOfTheTask = it.data?.fixer?.past_fixes as List<PastFixes>
                    it.data.fixer.document.let { document -> documentAdapter?.addAll(document) }
                    resumeData = it.data.fixer.resume
                    tvDescription.text = it.data.fixer.description
                }
            }

            bookFixer.observe(this@FixerProfileActivity) {
                if (it.success) {
                    startActivityForResult<OrderAcceptedActivity>(REQ_ORDER_CODE_LOST)
                    finish()
                }
            }
        }
    }

    private fun setUpUI() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.fixer_profile)
            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
            if (intent.hasExtra(FIXER_ID)) fixerId = intent.getIntExtra(FIXER_ID, 0)
            if (intent.hasExtra(JOB_STATUS_ID))
                if (intent.getIntExtra(
                        "JOB_STATUS_ID",
                        0
                    ) == 2 || intent.getIntExtra("JOB_STATUS_ID", 0) == 3
                ) {
                    btnBookFixer.gone()
                }

            fixerTasksPhotoAdapter = FixerTasksPhotoAdapter()
            rvJobPhotos.adapter = fixerTasksPhotoAdapter

            // documentAdapter = DocumentAdapter()
            //  rvDocumentList.adapter = documentAdapter

            viewModel.fixerProfile(FixerProfile(fixerId, jobId))
        }
    }

    private fun clickListener() {
        binding.run {
            btnChat.setOnClickListener(this@FixerProfileActivity)
            btnBookFixer.setOnClickListener(this@FixerProfileActivity)
            toolBar.imgBack.setOnClickListener(this@FixerProfileActivity)
            tvViewAll.setOnClickListener(this@FixerProfileActivity)
            tvSeeMore.setOnClickListener(this@FixerProfileActivity)
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
            R.id.btnChat -> {
                isChatCreate = true
                updateLoaderUI(true)
                SocketService.getInstance()?.sendCreateChat(fixerId, jobId)
            }
            R.id.btnBookFixer -> viewModel.bookFixer(BookFixer(jobId, 2, fixerId))
            R.id.imgBack -> onBackPressed()
            R.id.tvViewAll -> startActivity<PhotosTaskActivity>("photoOfTheTask" to photoOfTheTask)
            R.id.tvSeeMore -> {
                val uri = Uri.parse(resumeData)
                showDocument(uri)
            }
        }
    }

    private fun showDocument(photoUri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(photoUri, "image/*")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_ORDER_CODE_LOST -> {

                }
            }
        }
    }
}