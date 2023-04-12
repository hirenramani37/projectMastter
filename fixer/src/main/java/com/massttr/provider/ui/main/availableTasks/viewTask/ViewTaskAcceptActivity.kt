package com.massttr.provider.ui.main.availableTasks.viewTask

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.common.base.BaseActivity
import com.common.data.network.model.ViewJob
import com.common.data.network.model.request.JobStatus
import com.common.data.network.model.request.ViewTaskAccept
import com.massttr.provider.R
import com.massttr.provider.chat.Inbox
import com.massttr.provider.chat.FixerSocketService
import com.massttr.provider.databinding.ActivityViewTaskAcceptBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.showImage.ShowImageActivity
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.StartTasksActivity
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.startTasksSuccess.StartTaskSuccessActivity
import com.massttr.provider.ui.main.myUpcomingFix.taskDetails.EquipmentAdapter
import com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat.ChatActivity
import com.massttr.user.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber


@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class ViewTaskAcceptActivity :
    BaseActivity<ActivityViewTaskAcceptBinding>(R.layout.activity_view_task_accept),
    View.OnClickListener {

    private val viewModel: ViewTaskViewModel by viewModels()
    private var equipmentAdapter: EquipmentAdapter? = null
    private var fixerTaskPhotoAdapter: ViewTaskAdapter? = null
    private var jobId: Int? = 0
    private var job: ViewJob? = null
    private var mobileNumber: String = ""
    private var locationLatitude: String = ""
    private var locationLongitude: String = ""
    private var bannerImage: String = ""
    private var isChatCreate: Boolean = false

    companion object {
        const val JOB_ID = "JOB_ID"
        const val REMOVE = "REMOVE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUI()
        setObserver()
        initBusEvent()
        clickListener()
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@ViewTaskAcceptActivity) { handleError(it) }
            appLoader.observe(this@ViewTaskAcceptActivity) { updateLoaderUI(it) }
            viewTask.observe(this@ViewTaskAcceptActivity) {
                binding.run {
                    try {
                        mainView.visible()
                        tvJobTitle.text = it.job.title
                        tvJobContent.text = it.job.description
                        tvJobPrice.text = (it.job.price).plus(" QD")
                        tvFullTimeTask.text =
                            changeDateFormat(
                                it.job.appointment_date?:"",
                                "dd MMMM yyyy",
                                "EEE, d MMM"
                            ).plus(
                                " " + changeDateFormat(
                                    it.job.appointment_time?:"",
                                    "hh:mm a",
                                    "hh:mm"
                                )
                            )
                        //Fixer Profile
                        tvFixerLocation.text = it.job.user?.address
                        tvFixerName.text = it.job.user?.full_name ?: ""
                        bannerImage = it.job.banner_image?:""
                        Glide.with(this@ViewTaskAcceptActivity)
                            .load(bannerImage)
                            .placeholder(R.drawable.img_banner_placeholder)
                            .error(R.drawable.img_banner_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.IvMainPhoto)

                        loadImagesGlide(
                            it.job.banner_image?:"",
                            IvMainPhoto,
                            R.drawable.img_banner_placeholder
                        )
                        loadImage(it?.job?.user?.profile_picture.toString(), civProfile)
                        //civProfile.load(it?.job?.user?.profile_picture ?: "")

                        job = it.job
                        if (it.job.required_equipment != null) {
                            val equipment: List<String> =
                                it.job.required_equipment.split(",").toTypedArray().toList()
                            equipmentAdapter?.addAll(equipment)
                            rvEquipment.visible()
                            tvRequiredEquipment.visible()
                            viewDivider5.visible()
                        } else {
                            rvEquipment.gone()
                            tvRequiredEquipment.gone()
                            viewDivider5.gone()
                        }
                        fixerTaskPhotoAdapter?.addAll(it.job.job_image)
                        if (fixerTaskPhotoAdapter?.displayList?.isEmpty() == true) {
                            tvJobPhoto.gone()
                            rvJobPhotos.gone()
                            viewDivider5.gone()
                        } else {
                            tvJobPhoto.visible()
                            rvJobPhotos.visible()
                            viewDivider5.visible()
                        }
                        fixerTaskPhotoAdapter?.addAll(it.job.job_image)
                        tvJobPhoto.isVisible = fixerTaskPhotoAdapter?.displayList?.size != 0

                        mobileNumber = it.job.user?.mobile_no?:""
                        locationLatitude = it.job.location_latitude?:""
                        locationLongitude = it.job.location_longitude?:""

                        if (it.job.status == "Booked" || it.job.status == "Acceptere" || it.job.status == "Job started" || it.job.status == "Job startede") {
                            btnChat.gone()
                            btnAccept.gone()
                            llChatCall.visible()
                            tvAvailableJob.gone()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            interested.observe(this@ViewTaskAcceptActivity) {
                //  binding.btnInterested.gone()
                binding.btnChat.visible()
                interested.observe(this@ViewTaskAcceptActivity) {
                    binding.btnInterested.gone()
                    binding.btnChat.visible()

                }
            }
            changeJobStatus.observe(this@ViewTaskAcceptActivity) {
                startActivity<StartTaskSuccessActivity>(JOB_ACCEPT_ACTIVITY to JOB_ACCEPT_ACTIVITY)
                finish()
            }
        }
    }

    private fun loadImage(image: String, ivImage: CircleImageView) {
        ivImage.load(image) {
            placeholder(R.drawable.ic_placeholder)
            error(R.drawable.ic_placeholder)
        }
    }

    private fun clickListener() {
        binding.run {
            toolbar.imgBack.setOnClickListener(this@ViewTaskAcceptActivity)
            btnChat.setOnClickListener(this@ViewTaskAcceptActivity)
            btnAccept.setOnClickListener(this@ViewTaskAcceptActivity)
            tvFixerLocation.setOnClickListener(this@ViewTaskAcceptActivity)
            ivCall.setOnClickListener(this@ViewTaskAcceptActivity)
            ivChat.setOnClickListener(this@ViewTaskAcceptActivity)
            IvMainPhoto.setOnClickListener(this@ViewTaskAcceptActivity)

            fixerTaskPhotoAdapter?.setItemClickListener { view, i, viewJobImage ->
                when (view.id) {
                    R.id.ivPhotosTasks -> startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to viewJobImage.image)
                }
            }
        }
    }

    private fun setUpUI() {
        binding.toolbar.tvTitle.text = getString(R.string.task)

        if (intent.hasExtra(JOB_ID)) {
            val jobId = intent.getStringExtra(JOB_ID) ?: "0"
            viewModel.viewTask(ViewTaskAccept(jobId.toInt()))
        }

        if (intent.hasExtra("REMOVE")) {
            if (intent.getBooleanExtra("REMOVE", true)) {
                binding.btnAccept.gone()
            } else {
                binding.btnAccept.gone()
                binding.btnChat.gone()
            }
        }
        equipmentAdapter = EquipmentAdapter()
        binding.rvEquipment.adapter = equipmentAdapter
        fixerTaskPhotoAdapter = ViewTaskAdapter()
        binding.rvJobPhotos.adapter = fixerTaskPhotoAdapter


        if (intent.hasExtra(AVAILABLE_FIX_DETAIL)) {
            job = intent.getSerializableExtra(AVAILABLE_FIX_DETAIL) as ViewJob?
            viewModel.viewTask(ViewTaskAccept(job?.id ?: 0))
        } else if (intent.hasExtra(AVAILABLE_FIXDETAIL)) {
            jobId = intent.getIntExtra(AVAILABLE_FIXDETAIL, 0)
            viewModel.viewTask(ViewTaskAccept(jobId ?: 0))
        }

        if (intent.hasExtra(NOTIFICATION_FIXDETAIL)) {
            jobId = intent.getIntExtra(NOTIFICATION_FIXDETAIL, 0)
            viewModel.viewTask(ViewTaskAccept(jobId ?: 0))
        }

        if (intent.hasExtra(GOOGLETAG_FIXDETAIL)) {
            jobId = intent.getIntExtra(GOOGLETAG_FIXDETAIL, 0)
            viewModel.viewTask(ViewTaskAccept(jobId ?: 0))
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnChat -> {
                job?.let {
                    isChatCreate = true
                    updateLoaderUI(true)
                    FixerSocketService.getInstance()?.sendCreateChat(it.user_id?:0, it.id?:0)
                }
            }
            R.id.btnAccept -> {
                if (job?.status == "Task created") {
                    if (job?.status == "Task created") {
                        val jobStatus = JobStatus(
                            job?.id.toString().trim(),
                            PRM_JOB_ACCEPT_STATUS
                        )
                        viewModel.endOrStartTask(jobStatus)
                    } else {
                        startActivity<StartTasksActivity>(START_JOB_ACTIVITY to job?.id)
                        finish()
                    }
                }
            }
            R.id.tvFixerLocation -> { }
            R.id.ivCall -> this.call(mobileNumber)
            R.id.ivChat -> {
                job?.let {
                    isChatCreate = true
                    updateLoaderUI(true)
                    FixerSocketService.getInstance()?.sendCreateChat(it.user_id?:0, it.id?:0)
                }
            }
            R.id.IvMainPhoto -> startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to bannerImage)
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
}

