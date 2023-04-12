package com.massttr.provider.ui.main.myprofiles.helpCenter.faq

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityFaqBinding
import timber.log.Timber

class FaqActivity : BaseActivity<ActivityFaqBinding>(R.layout.activity_faq), View.OnClickListener {
    private lateinit var faqAdapter: FaqAdapter
    val viewModel: FaqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setObserver()
        clickListener()
    }

    private fun setObserver() {
        viewModel.run {
            appLoader.observe(this@FaqActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@FaqActivity) { handleError(it) }
            faq.observe(this@FaqActivity) {
                it.data.let { it1 -> faqAdapter.addAll(it1) }
            }
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.faq)
            Timber.e("lang: ${pref.isArabic}")
            faqAdapter = FaqAdapter(pref.isArabic)
            rvFaq.adapter = faqAdapter
            viewModel.faq()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}