package com.massttr.user.ui.main.myprofile.order_history.order_details.showImage


import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.common.data.network.model.JobImage
import com.massttr.user.R
import com.massttr.user.databinding.ActivityShowImageBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class ShowImageActivity : BaseActivity<ActivityShowImageBinding>(R.layout.activity_show_image),
    View.OnClickListener {
    private lateinit var sliderAdapter: ImageViewPagerAdapter

    companion object {
        const val IMAGE_LIST = "IMAGE_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        clickListener()
    }

    fun init() {
        binding.run {
            tvTitle.text = getString(R.string.photos_of_task)
            sliderAdapter = ImageViewPagerAdapter(this@ShowImageActivity)
            if (intent.hasExtra(IMAGE_LIST)) {
                val data = intent.getSerializableExtra(IMAGE_LIST) as ArrayList<JobImage>
                Timber.e("data: $data")
                sliderAdapter.addAll(intent.getSerializableExtra(IMAGE_LIST) as ArrayList<JobImage>)
            }
            viewPager.adapter = sliderAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }

    private fun clickListener() = binding.imgBack.setOnClickListener(this)
}