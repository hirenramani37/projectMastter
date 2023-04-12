package com.massttr.provider.ui.main.myprofiles.helpCenter

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityHelpCenterBinding
import com.massttr.provider.ui.main.myprofiles.helpCenter.contactUs.ContactUsActivity
import com.massttr.provider.ui.main.myprofiles.helpCenter.faq.FaqActivity
import com.massttr.provider.ui.main.myprofiles.helpCenter.privacyPolicy.PrivacyPolicyActivity
import com.massttr.user.utils.visible
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class HelpCenterActivity : BaseActivity<ActivityHelpCenterBinding>(R.layout.activity_help_center),
    View.OnClickListener {

    private val viewModel: HelpCenterViewModel by viewModels()
    private lateinit var helpCenterAdapter: HelpCenterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setUpObserver()
        clickListener()
    }


    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(this@HelpCenterActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@HelpCenterActivity) { handleError(it) }
            helpCenter.observe(this@HelpCenterActivity) {
                it.data.let { list ->
                    helpCenterAdapter.addAll(list)
                    binding.llFAQ.visible()
                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            tvFaq.setOnClickListener(this@HelpCenterActivity)
            btnContactUs.setOnClickListener(this@HelpCenterActivity)
            toolbar.imgBack.setOnClickListener(this@HelpCenterActivity)

            helpCenterAdapter.setItemClickListener { view, _, privacyPolicyResponse ->
                when (view.id) {
                    R.id.tvPrivacyPolicy -> {
                        startActivity<PrivacyPolicyActivity>(PrivacyPolicyActivity.HELP_CENTER to privacyPolicyResponse)
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.run {
            toolbar.tvTitle.text = getString(R.string.help_center)
            viewModel.helpCenter()

            helpCenterAdapter = HelpCenterAdapter(pref.isArabic, this@HelpCenterActivity)
            rvHelpCenter.adapter = helpCenterAdapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
//            R.id.tvPrivacyPolicy -> startActivity<PrivacyPolicyActivity>()
//            R.id.tvTermsCondition -> startActivity<TermsConditionActivity>()
            R.id.tvFaq -> startActivity<FaqActivity>()
            R.id.btnContactUs -> startActivity<ContactUsActivity>()
        }
    }
}