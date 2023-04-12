package com.massttr.user.ui.main.myprofile.help_center.terms_condition

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityTermsConditionBinding
import com.massttr.user.ui.main.myprofile.help_center.privacy_policy.PrivacyPolicyViewModel
import com.massttr.user.utils.toHtml

class TermsConditionActivity :
    BaseActivity<ActivityTermsConditionBinding>(R.layout.activity_terms_condition),
    View.OnClickListener {
    private val viewModel: PrivacyPolicyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniView()
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@TermsConditionActivity) { handleError(it) }
            appLoader.observe(this@TermsConditionActivity) { updateLoaderUI(it) }
            privacyPolicy.observe(this@TermsConditionActivity) { privacyPolicy ->
                privacyPolicy.data?.forEach { it ->
                    if (pref.isLanguage == true)
                        binding.tvTermsCondition.text = it.en_content.toHtml()
                    else
                        binding.tvTermsCondition.text = it.ar_content.toHtml()
                }
            }
        }
    }

    private fun clickListener() {
        binding.toolbar.imgBack.setOnClickListener(this)
    }

    private fun iniView() {
        binding.toolbar.tvTitle.text = getString(R.string.terms_condition)
        viewModel.privacyPolicy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}