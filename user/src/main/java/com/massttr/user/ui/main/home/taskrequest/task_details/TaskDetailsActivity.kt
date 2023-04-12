package com.massttr.user.ui.main.home.taskrequest.task_details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.common.data.network.model.Subcategory
import com.common.data.network.model.TaskBookJob
import com.common.data.network.model.request.BookJob
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTaskDetailsBinding
import com.massttr.user.databinding.TaskDetailsDialogBinding
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.FixerListActivity
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.FixerTaskPhotoAdapter
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class TaskDetailsActivity :
    BaseActivity<ActivityTaskDetailsBinding>(R.layout.activity_task_details),
    View.OnClickListener {

    private val viewModel: TaskDetailsViewModel by viewModels()
    private var taskTagAdapter: TaskTagAdapter? = null
    private var equipmentAdapter: EquipmentAdapter? = null
    private var fixerTaskPhotoAdapter: FixerTaskPhotoAdapter? = null
    private var taskBookJob: TaskBookJob? = null
    private var multipleImage = ArrayList<String>()
    private var equipment = ""
    private var jobId = 0
    private var bannerImage = ""

    companion object {
        const val TASK_BOOK_JOB = "TASK_BOOK_JOB"
        const val BANNER_IMAGE = "BANNER_IMAGE"
        const val MULTIPLE_IMAGE = "MULTIPLE_IMAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@TaskDetailsActivity) { handleError(it) }
            appLoader.observe(this@TaskDetailsActivity) { updateLoaderUI(it) }
            bookJob.observe(this@TaskDetailsActivity) {
                jobId = it.data?.id!!
                taskDialog()
            }
        }
    }

    private fun init() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.task_details)

            taskTagAdapter = TaskTagAdapter()
            equipmentAdapter = EquipmentAdapter()
            fixerTaskPhotoAdapter = FixerTaskPhotoAdapter()

            if (intent.hasExtra("TASK_BOOK_JOB")) {
                taskBookJob =
                    intent.getSerializableExtra("TASK_BOOK_JOB") as TaskBookJob?
                binding.run {
                    loadImages(
                        taskBookJob?.bannerUrl.toString(),
                        IvMainPhoto,
                        R.drawable.ic_placeholder
                    )
                    tvJobTitle.text = taskBookJob?.title
                    tvJobContent.text = taskBookJob?.taskDescription
                    tvJobPrice.text = taskBookJob?.dkkPrice.plus("IQD")
                    tvDateAndTime.text = changeDateFormat(
                        taskBookJob?.date.toString(),
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    ).uppercase(Locale.getDefault()).plus(", " + taskBookJob?.time.toString())

                    if (taskBookJob?.priceType == 0) {
                        tvHourlyRate.gone()
                        tvHourlyPrice.gone()
                        viewDivider6.gone()
                        tvTaskHours.gone()
                        tvTaskHoursPrice.gone()
                    } else {
                        tvTaskHoursPrice.text = taskBookJob?.hours
                        tvHourlyPrice.text = taskBookJob?.hourlyRate
                    }
                }
                //Category List
                taskTagAdapter?.addAll(taskBookJob?.subCateArrayList as ArrayList<Subcategory>)
                rvJobTag.adapter = taskTagAdapter

                //Equipment List
                if (taskBookJob?.requiredTool.toString().isEmpty()) {
                    tvRequiredEquipment.gone()
                    rvEquipment.gone()
                    viewDivider5.gone()
                } else {
                    equipment = taskBookJob?.requiredTool.toString()
                    val requiredEquipment = equipment.split(" ")
                    requiredEquipment.forEach {
                        equipmentAdapter?.addItem(it)
                    }
                    rvEquipment.adapter = equipmentAdapter
                }

                //TaskPhotos List
                if (taskBookJob?.taskPhotos?.isEmpty() == true) {
                    tvJobPhoto.gone()
                } else {
                    fixerTaskPhotoAdapter?.addAll(taskBookJob?.taskPhotos as ArrayList<File>)
                    rvJobPhotos.adapter = fixerTaskPhotoAdapter
                }
            }

            if (intent.hasExtra(BANNER_IMAGE)) {
                bannerImage = intent.getStringExtra(BANNER_IMAGE).toString()
            }
            if (intent.hasExtra(MULTIPLE_IMAGE)) {
                multipleImage = intent.getSerializableExtra(MULTIPLE_IMAGE) as ArrayList<String>
                Timber.e("multipleImage: $multipleImage")
            }
        }
    }


    private fun clickListener() {
        binding.run {
            btnCreateTask.setOnClickListener(this@TaskDetailsActivity)
            toolBar.imgBack.setOnClickListener(this@TaskDetailsActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCreateTask -> {
                binding.run {
                    viewModel.bookJob(
                        BookJob(
                            taskBookJob?.address.toString(),
                            taskBookJob?.latitude.toString(),
                            taskBookJob?.longitude.toString(),
                            taskBookJob?.title.toString(),
                            taskBookJob?.taskDescription.toString(),
                            taskBookJob?.category_id!!,
                            taskBookJob?.subcategory_ids.toString().removeBrackets(),
                            if (taskBookJob?.priceType == 0) "0" else "1",
                            taskBookJob?.dkkPrice.toString(),
                            taskBookJob?.date.toString(),
                            taskBookJob?.time.toString(),
                            if (taskBookJob?.priceType == 1) taskBookJob?.hours.toString() else "0",
                            taskBookJob?.requiredTool?.replace(" ", ",").toString(),
                            multipleImage.toString().removeBrackets(),
                            bannerImage,
                        )
                    )
                }

                //startActivity<TaskDetailsActivity>(
//                        TaskDetailsActivity.BANNER_IMAGE_URL to uri.toString(),
//                        TaskDetailsActivity.BOOK_TITLE to etTaskTitle.getTextString(),
//                        TaskDetailsActivity.BOOK_SUB_CATEGORY_LIST to subCateArrayList,
//                        TaskDetailsActivity.BOOK_PHOTOS_LIST to selectTaskPhotos?.displayList,
//                        TaskDetailsActivity.BOOK_DESCRIPTION to etTaskDescription.getTextString(),
//                        TaskDetailsActivity.BOOK_JOB_DATE to tvDateTimeLine.text.toString(),
//                        TaskDetailsActivity.BOOK_JOB_TIME to tvTimeTimeLine.text.toString(),
//                        TaskDetailsActivity.BOOK_JOB_HOURLY_RATE to etHourlyRate.getTextString(),
//                        TaskDetailsActivity.BOOK_JOB_TASK_HOURS_RATE to etHours.getTextString(),
//                        TaskDetailsActivity.BOOK_JOB_TASK_PRICE to tvDkkPrice.text.toString(),
//                        TaskDetailsActivity.BOOK_TYPE to priceType,
//                        TaskDetailsActivity.JOB_ID to jobId,
//                        TaskDetailsActivity.REQUIRED_EQUIPMENT_LIST to etRequiredTool.getTextString()
//                    )
//                    finish()
//                binding.run {
//                    viewModel.bookJob(
//                        BookJob(
//                            address,
//                            latitude,
//                            longitude,
//                            tvJobTitle.text.toString(),
//                            tvJobContent.text.toString(),
//                        )
//                    )
//                }

            }
            R.id.imgBack -> onBackPressed()
        }
    }


    private fun taskDialog() {
        val dialog = Dialog(this@TaskDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        val binding: TaskDetailsDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this@TaskDetailsActivity),
            R.layout.task_details_dialog,
            null,
            false
        )

        binding.btnConfirm.setOnClickListener {
            startActivity<FixerListActivity>(
                FixerListActivity.JOB_ID to jobId,
                FixerListActivity.TASK_PHOTO_LIST to fixerTaskPhotoAdapter?.displayList
            )
            finish()
            dialog.dismiss()
        }

        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.show()
    }
}


