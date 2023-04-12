package com.massttr.user.ui.main.myprofile.help_center.privacy_policy

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityPrivacyPolicyBinding
import com.massttr.user.utils.toHtml

class PrivacyPolicyActivity :
    BaseActivity<ActivityPrivacyPolicyBinding>(R.layout.activity_privacy_policy),
    View.OnClickListener {

    private val viewModel: PrivacyPolicyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@PrivacyPolicyActivity) { handleError(it) }
            appLoader.observe(this@PrivacyPolicyActivity) { updateLoaderUI(it) }
            privacyPolicy.observe(this@PrivacyPolicyActivity) { privacyPolicy ->
                privacyPolicy.data?.forEach { it ->
                    if (pref.isLanguage == true)
                        binding.tvPrivacyPolicy.text = it.en_content.toHtml()
                    else
                        binding.tvPrivacyPolicy.text = it.ar_content.toHtml()
                }
            }
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.privacy_policy)
        viewModel.privacyPolicy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}