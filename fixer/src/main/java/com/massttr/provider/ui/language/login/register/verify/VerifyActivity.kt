package com.massttr.provider.ui.language.login.register.verify

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityVerifyBinding
import com.massttr.provider.ui.language.login.register.verify.fixerRegistration.FixerRegistrationActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class VerifyActivity : BaseActivity<ActivityVerifyBinding>(R.layout.activity_verify),
    View.OnClickListener {
    private var fullName: String = ""
    private var mobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        clickListener()
    }

    private fun initView() {
        if (intent.hasExtra(OTPVerificationActivity.FULL_NAME)) {
            fullName = intent.getStringExtra(OTPVerificationActivity.FULL_NAME)?:""
        }
        if(intent.hasExtra(OTPVerificationActivity.MOBILE_NUMBER)){
            mobile = intent.getStringExtra(OTPVerificationActivity.MOBILE_NUMBER)?:""
        }
    }

    private fun clickListener() {
        binding.btnDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDone -> {
                startActivity<FixerRegistrationActivity>(
                    FixerRegistrationActivity.FULL_NAME to fullName,
                FixerRegistrationActivity.MOBILE_NUMBER to mobile)
                finishAffinity()
            }
        }
    }
}

