package com.massttr.user.ui.main.myprofile.order_history.order_details.complete_task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.common.data.network.model.JobImage
import com.common.data.network.model.SelectPhotos
import com.massttr.user.R
import com.massttr.user.databinding.ActivityCompleteTaskBinding
import com.massttr.user.ui.main.home.taskrequest.task_details.TaskDetailsActivity
import com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer.give_review.rating.RatingActivity
import com.massttr.user.ui.main.myprofile.order_history.order_details.OrderDetailsActivity
import org.jetbrains.anko.startActivity

class CompleteTaskActivity :
    BaseActivity<ActivityCompleteTaskBinding>(R.layout.activity_complete_task),
    View.OnClickListener {
    private var jobId = 0
    private var fixerId = 0
    private var photosOfTheTask = ArrayList<JobImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    companion object {
        const val JOB_ID = "JOB_ID"
        const val FIXER_ID = "FIXER_ID"
        const val PHOTOS_OF_TASK = "PHOTOS_OF_THE_TASK"
        const val REQ_COMPLETE_TASK = 78
    }

    private fun clickListener() {
        binding.run {
            btnGiveReview.setOnClickListener(this@CompleteTaskActivity)
            toolBar.imgBack.setOnClickListener(this@CompleteTaskActivity)
        }
    }

    private fun initView() {
        binding.run {
            toolBar.tvTitle.text = getString(R.string.complete_task)
            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
            if (intent.hasExtra(FIXER_ID)) fixerId = intent.getIntExtra(FIXER_ID, 0)
            if (intent.hasExtra(PHOTOS_OF_TASK))
                photosOfTheTask = intent.getSerializableExtra(PHOTOS_OF_TASK) as ArrayList<JobImage>
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnGiveReview -> {
                val intent = Intent(this, RatingActivity::class.java)
                intent.putExtra(RatingActivity.JOB_ID, jobId)
                intent.putExtra(RatingActivity.FIXER_ID, fixerId)
                intent.putExtra(RatingActivity.PHOTOS_OF_TASK, photosOfTheTask)
                startActivityForResult(intent, REQ_COMPLETE_TASK)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_COMPLETE_TASK -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}