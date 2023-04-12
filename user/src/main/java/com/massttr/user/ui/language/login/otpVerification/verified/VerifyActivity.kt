package com.massttr.user.ui.language.login.otpVerification.verified

import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.massttr.user.R
import com.massttr.user.databinding.ActivityVerifyBinding
import com.massttr.user.ui.language.login.otpVerification.OTPVerificationActivity
import com.massttr.user.ui.language.login.register.create_profile.CreateProfileActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class VerifyActivity : BaseActivity<ActivityVerifyBinding>(R.layout.activity_verify),
    View.OnClickListener {
    var fullName: String = ""
    var mobileNumber: String = ""
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
            mobileNumber = intent.getStringExtra(OTPVerificationActivity.MOBILE_NUMBER)?:""
        }
    }

    private fun clickListener() {
        binding.btnDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDone -> {
                startActivity<CreateProfileActivity>(
                    CreateProfileActivity.FULL_NAME to fullName,
                    CreateProfileActivity.MOBILE_NUMBER to mobileNumber
                )
                finish()
            }
        }
    }
}