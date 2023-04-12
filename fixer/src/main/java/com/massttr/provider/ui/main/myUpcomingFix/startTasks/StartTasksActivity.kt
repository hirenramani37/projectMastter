package com.massttr.provider.ui.main.myUpcomingFix.startTasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.JobImage
import com.common.data.network.model.request.DeleteJobPhoto
import com.common.data.network.model.request.JobStatus
import com.github.dhaval2404.imagepicker.ImagePicker
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityStartTasksBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.showImage.ShowImageActivity
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.startTasksSuccess.StartTaskSuccessActivity
import com.massttr.provider.ui.main.myUpcomingFix.startTasks.task.GoodJobActivity
import com.massttr.provider.ui.main.myprofiles.completedTasks.CompletedTaskViewModel
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.MultipartBody
import org.jetbrains.anko.startActivity
import java.io.File

@OptIn(DelicateCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class StartTasksActivity : BaseActivity<ActivityStartTasksBinding>(R.layout.activity_start_tasks),
    View.OnClickListener {
    private val viewModel: CompletedTaskViewModel by viewModels()
    private lateinit var adapter: StartTasksAdapter
    private lateinit var jobPicList: ArrayList<JobImage>
    private var jobId: Int = 0
    private var jobEnd: Boolean = false

    companion object {
        private const val REQ_JOB_IMAGE = 78
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
        setUpObserver()
    }

    private fun clickListener() {
        binding.run {
            btnContinue.setOnClickListener(this@StartTasksActivity)
            toolbar.imgBack.setOnClickListener(this@StartTasksActivity)
            rlAdd.setOnClickListener(this@StartTasksActivity)
        }
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.start_task)
            if (intent.hasExtra(START_JOB_ACTIVITY)) {
                jobId = intent.getIntExtra(START_JOB_ACTIVITY, 0)
                toolbar.tvTitle.text = getString(R.string.start_task)
            } else if (intent.hasExtra(JOB_END_ACTIVITY)) {
                toolbar.tvTitle.text = getString(R.string.end_task)
                jobId = intent.getIntExtra(JOB_END_ACTIVITY, 0)
                jobEnd = true
            }

            adapter = StartTasksAdapter(this@StartTasksActivity)
            rvPhotosTask.adapter = adapter

//            jobPicList = arrayListOf()
//            jobPicList.add(JobImage(0, 0, "", 0, "", ""))
            updateUI(jobEnd)

            adapter.setItemClickListener { view, position, jobImage ->
                when (view.id) {
                    R.id.ivJobPhoto -> if (position == 0) pickFromGallery()
                    R.id.imgClose -> {
                        try {
                            viewModel.deleteJobImage(DeleteJobPhoto(jobImage.id.toString()))
                            deletePhoto(position, jobImage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        // deletePhotoAPI(availableFixItem.id)
                    }
                    R.id.ivPhotosTasks -> {
                        startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to jobImage.image)
                    }
                }
            }
        }
    }

    private fun deletePhoto(jobImage: Int, job: JobImage) {
        try {
            adapter.displayList.remove(job)
            adapter.notifyItemChanged(jobImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(this@StartTasksActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@StartTasksActivity) { handleError(it) }
            beforeImage.observe(this@StartTasksActivity) {
                adapter.addAll(it)
            }
            afterImage.observe(this@StartTasksActivity) {
                adapter.addAll(it)
            }
            jobUpload.observe(this@StartTasksActivity) {
                adapter.displayList.clear()
                it.forEach {
                    adapter.displayList.add(it)
                }
                //  adapter.addAll(it)
                adapter.notifyDataSetChanged()
            }

            changeJobStatus.observe(this@StartTasksActivity) {
                if (jobEnd) {
                    startActivity<GoodJobActivity>()
                    finish()
                } else {
                    startActivity<StartTaskSuccessActivity>()
                    finish()
                }
            }

            uploadProfileImage.observe(this@StartTasksActivity) {
                it.data.forEach {
                    uploadJobImage(it, jobEnd)
                }
            }
            bookJobPhotoDelete.observe(this@StartTasksActivity) {
                //  updateUI(jobEnd)
            }
        }
    }

    private fun updateUI(endJob: Boolean) {
        // adapter.addAll(jobPicList)
        getJobImage(endJob)
    }


    private fun getJobImage(endJob: Boolean) {
        if (endJob) {
            val jobImageAfter = com.common.data.network.model.request.JobImage(
                jobId,
                2
            )
            viewModel.getAfterJobImage(jobImageAfter)
        } else {
            val jobImageBefore = com.common.data.network.model.request.JobImage(
                jobId,
                1
            )
            viewModel.getBeforeJobImage(jobImageBefore)
        }
    }

    private fun pickFromGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop(1F, 1F)
            .start(REQ_JOB_IMAGE)
//            .crop(16f, 9f) // 1:1 , 3:4 , 3:2 , 16:9 -> if 'Original' image to remove Crop
//            .start { resultCode, data ->
//                if (resultCode == Activity.RESULT_OK) {
//                    //Image Uri will not be null for RESULT_OK
////                    val fileUri = data?.data
//                    //You can get File object from intent
//                    val file: File? = ImagePicker.getFile(data)
//                    //You can also get File Path from intent
////                    val filePath = ImagePicker.getFilePath(data)
//
//                    if (file != null) {
//                        uploadJobImage(file, jobEnd)
//                    }
//
//                } else if (resultCode == ImagePicker.RESULT_ERROR) {
//                    toast(ImagePicker.getError(data))
//                } else {
//                    toast("Task Cancelled")
//                }
//            }
    }

    private fun uploadJobImage(selected_file: String, endJob: Boolean) {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart(PRM_IMAGE, selected_file)
        builder.addFormDataPart("env", "test")
        if (endJob) {
            builder.addFormDataPart(PRM_JOB_ID, jobId.toString())
            builder.addFormDataPart(PRM_TYPE, PRM_END_JOB_TYPE)
        } else {
            builder.addFormDataPart(PRM_JOB_ID, jobId.toString())
            builder.addFormDataPart(PRM_TYPE, PRM_START_JOB_TYPE)
        }

        viewModel.jobUpload(builder.build())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnContinue -> {
                if (jobEnd) {
                    val jobStatus = JobStatus(
                        jobId.toString(),
                        PRM_JOB_COMPLETED
                    )
                    viewModel.endOrStartTask(jobStatus)
                } else {
                    val jobStatus = JobStatus(
                        jobId.toString(),
                        PRM_JOB_STARTED
                    )
                    viewModel.endOrStartTask(jobStatus)
                }
            }
            R.id.rlAdd -> pickFromGallery()
        }
    }

    private fun commonImageApi(file: File) {
        val builder = file.multipartImageBody("image[]")
        builder.addFormDataPart("dir", "start_task")
        viewModel.uploadJobImage(builder.build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_JOB_IMAGE -> {
                    //val file: File? = ImagePicker.getFile(data)
                    //You can also get File Path from intent
//                    val filePath = ImagePicker.getFilePath(data)
                    val file = File(PathUtil.getPath(this, data?.data))
                    commonImageApi(file)
                }
            }
        }
    }
}
