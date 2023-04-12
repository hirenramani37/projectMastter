package com.massttr.user.ui.language.login.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.Registration
import com.massttr.user.BuildConfig
import com.massttr.user.R
import com.massttr.user.databinding.ActivityRegisterBinding
import com.massttr.user.ui.language.login.LoginActivity
import com.massttr.user.ui.language.login.otpVerification.OTPVerificationActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import timber.log.Timber

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class RegistrationActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register),
    View.OnClickListener {
    private val viewModel: RegistrationActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGlobal.getFcmToken(this) {}
        setUpObserver()
        clickListener()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@RegistrationActivity) { handleError(it) }
            appLoader.observe(this@RegistrationActivity) { updateLoaderUI(it) }
            viewModel.register.observe(this@RegistrationActivity) {
                Toast.makeText(
                    this@RegistrationActivity,
                    it.data?.otp.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                pref.authToken = it.data?.token
                startActivity<OTPVerificationActivity>(
                    OTPVerificationActivity.FULL_NAME to binding.etFullName.text.toString(),
                    OTPVerificationActivity.MOBILE_NUMBER to binding.etMobileNumber.text.toString()
                )
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnRegistration.setOnClickListener(this@RegistrationActivity)
            lLFullName.setOnClickListener(this@RegistrationActivity)
            lLPhoneNumber.setOnClickListener(this@RegistrationActivity)
            etFullName.setOnClickListener(this@RegistrationActivity)
            etMobileNumber.setOnClickListener(this@RegistrationActivity)
            tvLogin.setOnClickListener(this@RegistrationActivity)
        }
    }

    override fun onClick(v: View?) {
        binding.run {
            when (v?.id) {
                R.id.btnRegistration -> validate()
                R.id.etFullName -> {
                    lLFullName.setBackgroundResource(R.drawable.bg_with_stroke)
                    lLPhoneNumber.setBackgroundResource(R.drawable.edit_box_bg)
                }
                R.id.etMobileNumber -> {
                    lLPhoneNumber.setBackgroundResource(R.drawable.bg_with_stroke)
                    lLFullName.setBackgroundResource(R.drawable.edit_box_bg)
                }
                R.id.tvLogin -> {
                    startActivity<LoginActivity>()
                    finishAffinity()
                }
            }
        }
    }

    private fun validate() {
        binding.run {
            when {
                etFullName.isBlank() -> setShakeError(getString(R.string.please_enter_first_name))
                etMobileNumber.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                !etMobileNumber.text.toString().isMobileNumberValid -> setShakeError(getString(R.string.please_valid_phone))
                pref.fcmToken.isNullOrEmpty() -> AppGlobal.getFcmToken(this@RegistrationActivity) { register() }
                else -> register()
            }
        }
    }

    private fun register() {
        binding.run {
            viewModel.register(
                Registration(
                    etFullName.getTextString(),
                    etMobileNumber.getTextString(),
                    "A",
                    pref.fcmToken.orEmpty(),
                    BuildConfig.VERSION_NAME,
                    1
                )
            )
        }
    }
}
