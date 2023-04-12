package com.massttr.user.ui.main.myprofile.help_center.faq

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityFaqBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class FaqActivity : BaseActivity<ActivityFaqBinding>(R.layout.activity_faq), View.OnClickListener {

    private val viewModel: FaqActivityViewModel by viewModels()
    private lateinit var faqAdapter: FaqAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@FaqActivity) { handleError(it) }
            appLoader.observe(this@FaqActivity) { updateLoaderUI(it) }
            faq.observe(this@FaqActivity) {
                it.data?.let { it1 -> faqAdapter.addAll(it1) }
            }
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.faq)
            viewModel.faq()
            faqAdapter = FaqAdapter()
            rvFaq.adapter = faqAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}