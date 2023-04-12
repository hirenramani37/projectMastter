package com.massttr.provider.ui.main.availableTasks.viewTask.showImage

import android.os.Bundle
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityShowImageBinding
import com.massttr.user.utils.loadChatMediaImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ShowImageActivity : BaseActivity<ActivityShowImageBinding>(R.layout.activity_show_image) {

    companion object {
        const val SHOW_IMAGE = "SHOW_IMAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.photos_of_the_task)
            if (intent.hasExtra(SHOW_IMAGE))
                intent.getStringExtra(SHOW_IMAGE)?.let { touchImageView.loadChatMediaImage(it) }

            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }
    }
}