package com.massttr.provider.ui.main.myprofiles.helpCenter.privacyPolicy

import android.os.Bundle
import android.text.Html
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.common.base.BaseActivity
import com.common.data.network.model.PrivacyPolicyResponse
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityPrivacyPolicyBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class PrivacyPolicyActivity :
    BaseActivity<ActivityPrivacyPolicyBinding>(R.layout.activity_privacy_policy),
    View.OnClickListener {

    companion object {
        const val HELP_CENTER = "HELP_CENTER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun clickListener() = binding.toolbar.imgBack.setOnClickListener(this)


    private fun initView() {
        binding.toolbar.tvTitle.text = getString(R.string.privacy_policy)

        if (intent.hasExtra(HELP_CENTER)) {
            val data = intent.getSerializableExtra(HELP_CENTER) as PrivacyPolicyResponse
            data.let {
                if (pref.isArabic == true) {
                    binding.tvPrivacyPolicy.text = Html.fromHtml(it.ar_content)
                    binding.toolbar.tvTitle.text = it.ar_name
                } else {
//                    binding.tvPrivacyPolicy.text = it.en_content.toHtml()
                    binding.tvPrivacyPolicy.text = Html.fromHtml(it.en_content)
                    binding.toolbar.tvTitle.text = it.en_name
                }
                Glide.with(this)
                    .load(it.inpage_image)
                    .placeholder(R.drawable.img_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgPrivacyPolicy)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> onBackPressed()
        }
    }
}
