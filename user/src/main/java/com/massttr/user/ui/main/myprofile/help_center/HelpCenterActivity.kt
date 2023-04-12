package com.massttr.user.ui.main.myprofile.help_center


import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityHelpCenterBinding
import com.massttr.user.ui.main.myprofile.help_center.contactus.ContactUsActivity
import com.massttr.user.ui.main.myprofile.help_center.faq.FaqActivity
import com.massttr.user.ui.main.myprofile.help_center.privacy_policy.PrivacyPolicyActivity
import com.massttr.user.ui.main.myprofile.help_center.terms_condition.TermsConditionActivity
import org.jetbrains.anko.startActivity

class HelpCenterActivity : BaseActivity<ActivityHelpCenterBinding>(R.layout.activity_help_center),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.run {
            tvPrivacyPolicy.setOnClickListener(this@HelpCenterActivity)
            tvTermsCondition.setOnClickListener(this@HelpCenterActivity)
            tvFaq.setOnClickListener(this@HelpCenterActivity)
            btnContactUs.setOnClickListener(this@HelpCenterActivity)
            toolbar.imgBack.setOnClickListener(this@HelpCenterActivity)
        }
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.help_center)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
            R.id.tvPrivacyPolicy -> startActivity<PrivacyPolicyActivity>()
            R.id.tvTermsCondition -> startActivity<TermsConditionActivity>()
            R.id.tvFaq -> startActivity<FaqActivity>()
            R.id.btnContactUs -> startActivity<ContactUsActivity>()
        }
    }
}