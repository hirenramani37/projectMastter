package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.book_fixer.give_review.rating

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.JobImage
import com.common.data.network.model.request.Rating
import com.massttr.user.utils.*
import com.massttr.user.R
import com.massttr.user.databinding.ActivityRatingBinding
import com.massttr.user.ui.main.myprofile.order_history.order_details.complete_task.TaskCompleteActivity
import org.jetbrains.anko.startActivityForResult

class RatingActivity : BaseActivity<ActivityRatingBinding>(R.layout.activity_rating),
    View.OnClickListener {
    private val viewModel: RatingActivityViewModel by viewModels()
    private lateinit var photosAdapter: PhotosTaskAdapter
    private var photosList = ArrayList<JobImage>()
    private var jobId = 0
    private var fixerId = 0
    private var ratingNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    companion object {
        const val JOB_ID = "JOB_ID"
        const val FIXER_ID = "FIXER_ID"
        const val PHOTOS_OF_TASK = "PHOTOS_OF_TASK"
        const val REQ_COMPLETE_RATING = 78
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@RatingActivity) { handleError(it) }
            appLoader.observe(this@RatingActivity) { updateLoaderUI(it) }
            rating.observe(this@RatingActivity) {
                if (it.success) {
                    it.message.showSuccessToast()
                    startActivityForResult<TaskCompleteActivity>(REQ_COMPLETE_RATING)
                } else
                    it.message.showErrorToast()
            }
        }
    }

    private fun clickListener() {
        binding.run {
            rat1.setOnClickListener(this@RatingActivity)
            rat2.setOnClickListener(this@RatingActivity)
            rat3.setOnClickListener(this@RatingActivity)
            rat4.setOnClickListener(this@RatingActivity)
            rat5.setOnClickListener(this@RatingActivity)
            btnSubmit.setOnClickListener(this@RatingActivity)
            toolbar.imgBack.setOnClickListener(this@RatingActivity)
        }
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.rating)

            if (intent.hasExtra(JOB_ID)) jobId = intent.getIntExtra(JOB_ID, 0)
            if (intent.hasExtra(FIXER_ID)) fixerId = intent.getIntExtra(FIXER_ID, 0)
            if (intent.hasExtra(PHOTOS_OF_TASK))
                photosList = intent.getSerializableExtra(PHOTOS_OF_TASK) as ArrayList<JobImage>

            photosAdapter = PhotosTaskAdapter()
            rvPhotoTask.adapter = photosAdapter
            rvPhotoTaskCompleted.adapter = photosAdapter
            photosAdapter.addAll(photosList)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.btnSubmit -> validation()
            R.id.rat1 ->
                binding.run {
                    ratingNumber = 1
                    rat1.setImageResource(R.drawable.ic_star)
                    rat2.setImageResource(R.drawable.ic_star_gray)
                    rat3.setImageResource(R.drawable.ic_star_gray)
                    rat4.setImageResource(R.drawable.ic_star_gray)
                    rat5.setImageResource(R.drawable.ic_star_gray)
                }
            R.id.rat2 ->
                binding.run {
                    ratingNumber = 2
                    rat1.setImageResource(R.drawable.ic_star)
                    rat2.setImageResource(R.drawable.ic_star)
                    rat3.setImageResource(R.drawable.ic_star_gray)
                    rat4.setImageResource(R.drawable.ic_star_gray)
                    rat5.setImageResource(R.drawable.ic_star_gray)
                }
            R.id.rat3 ->
                binding.run {
                    ratingNumber = 3
                    rat1.setImageResource(R.drawable.ic_star)
                    rat2.setImageResource(R.drawable.ic_star)
                    rat3.setImageResource(R.drawable.ic_star)
                    rat4.setImageResource(R.drawable.ic_star_gray)
                    rat5.setImageResource(R.drawable.ic_star_gray)
                }
            R.id.rat4 ->
                binding.run {
                    ratingNumber = 4
                    rat1.setImageResource(R.drawable.ic_star)
                    rat2.setImageResource(R.drawable.ic_star)
                    rat3.setImageResource(R.drawable.ic_star)
                    rat4.setImageResource(R.drawable.ic_star)
                    rat5.setImageResource(R.drawable.ic_star_gray)
                }
            R.id.rat5 ->
                binding.run {
                    ratingNumber = 5
                    rat1.setImageResource(R.drawable.ic_star)
                    rat2.setImageResource(R.drawable.ic_star)
                    rat3.setImageResource(R.drawable.ic_star)
                    rat4.setImageResource(R.drawable.ic_star)
                    rat5.setImageResource(R.drawable.ic_star)
                }
        }
    }

    private fun validation() {
        binding.run {
            when {
                etFeedback.isBlank() -> etFeedback.setShakeError(getString(R.string.enter_your_feedback))
                else ->
                    viewModel.ratingUser(
                        Rating(
                            fixerId,
                            jobId,
                            ratingNumber,
                            etFeedback.getTextString()
                        )
                    )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_COMPLETE_RATING -> {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}