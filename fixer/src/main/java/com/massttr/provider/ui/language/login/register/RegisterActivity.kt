package com.massttr.provider.ui.language.login.register

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.CreateRegistration
import com.massttr.provider.BuildConfig
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityRegisterBinding
import com.massttr.provider.ui.language.login.LoginActivity
import com.massttr.provider.ui.language.login.register.verify.OTPVerificationActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register),
    View.OnClickListener {

    private val viewModel: RegisterActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGlobal.getFcmToken(this) {}
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@RegisterActivity) { handleError(it) }
            appLoader.observe(this@RegisterActivity) { updateLoaderUI(it) }
            register.observe(this@RegisterActivity) {
                pref.authToken = it.data?.api_token
                startActivity<OTPVerificationActivity>(
                    OTPVerificationActivity.FULL_NAME to binding.etFullName.text.toString(),
                    OTPVerificationActivity.MOBILE_NUMBER to binding.etMobileNumber.text.toString(),
                    OTPVerificationActivity.OTP to it.data?.otp.toString()
                )
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnRequestOTP.setOnClickListener(this@RegisterActivity)
            lLEdittext1.setOnClickListener(this@RegisterActivity)
            lLEdittext.setOnClickListener(this@RegisterActivity)
            etFullName.setOnClickListener(this@RegisterActivity)
            etMobileNumber.setOnClickListener(this@RegisterActivity)
            tvLogin.setOnClickListener(this@RegisterActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRequestOTP -> validate()
            R.id.etFullName -> {
                binding.lLEdittext1.setBackgroundResource(R.drawable.bg_with_stroke)
                binding.lLEdittext.setBackgroundResource(R.drawable.edit_box_bg)
            }
            R.id.etMobileNumber -> {
                binding.lLEdittext.setBackgroundResource(R.drawable.bg_with_stroke)
                binding.lLEdittext1.setBackgroundResource(R.drawable.edit_box_bg)
            }
            R.id.lLEdittext1 -> {
                binding.lLEdittext1.setBackgroundResource(R.drawable.bg_with_stroke)
                binding.lLEdittext.setBackgroundResource(R.drawable.edit_box_bg)
            }
            R.id.lLEdittext -> {
                binding.lLEdittext.setBackgroundResource(R.drawable.bg_with_stroke)
                binding.lLEdittext1.setBackgroundResource(R.drawable.edit_box_bg)
            }
            R.id.tvLogin -> {
                startActivity<LoginActivity>()
                finishAffinity()
            }
        }
    }

    private fun validate() {
        binding.run {
            when {
                etFullName.isBlank() -> setShakeError(getString(R.string.please_enter_name))
                etMobileNumber.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                !etMobileNumber.text.toString().isMobileNumberValid -> setShakeError(getString(R.string.enter_valid_mobile_number))
                pref.fcmToken.isNullOrEmpty() -> AppGlobal.getFcmToken(this@RegisterActivity) { register() }
                else -> register()
            }
        }
    }

    private fun register() {
        viewModel.register(
            CreateRegistration(
                binding.etMobileNumber.getTextString(),
                "A",
                binding.etFullName.getTextString(),
                pref.fcmToken.orEmpty(),
                BuildConfig.VERSION_NAME,
                1
            )
        )
    }
}
