package com.massttr.user.ui.main.home.taskrequest.task_details.fixer_list.fixer_profile.photoTask

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.common.data.network.model.PastFixes
import com.massttr.user.R
import com.massttr.user.databinding.ActivityPhotosTaskBinding
import timber.log.Timber

class PhotosTaskActivity : BaseActivity<ActivityPhotosTaskBinding>(R.layout.activity_photos_task),
    View.OnClickListener {
    private var photosTaskAdapter: PhotosTaskAdapter? = null
    private lateinit var  photoOfTheTask : List<PastFixes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpUI()
        clickListener()
    }

    private fun init(){
        if(intent.hasExtra("photoOfTheTask")){
            photoOfTheTask = intent.getSerializableExtra("photoOfTheTask") as List<PastFixes>
            Timber.e("photoOfTheTask $photoOfTheTask")
        }
    }

    private fun clickListener() {

        binding.toolbar.imgBack.setOnClickListener(this@PhotosTaskActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> {
                onBackPressed()
            }
        }
    }

    private fun setUpUI() {
        binding.run {
            binding.toolbar.tvTitle.text = getString(R.string.photos_of_the_task)
            photosTaskAdapter = PhotosTaskAdapter()
            rvPhotoGallery.adapter = photosTaskAdapter
            photosTaskAdapter?.addAll(photoOfTheTask)
        }
    }

}