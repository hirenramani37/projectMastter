package com.massttr.user.ui.language.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.Login
import com.massttr.user.BuildConfig
import com.massttr.user.R
import com.massttr.user.databinding.ActivityLoginBinding
import com.massttr.user.ui.language.login.otpVerification.OTPVerificationActivity
import com.massttr.user.ui.language.login.register.RegistrationActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login),
    View.OnClickListener {

    private val viewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGlobal.getFcmToken(this) {}
        setUpObserver()
        clickListeners()
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@LoginActivity) { handleError(it) }
            appLoader.observe(this@LoginActivity) { updateLoaderUI(it) }
            login.observe(this@LoginActivity) {
                pref.authToken = it.data?.token
                toast(it.data?.otp.toString())
                startActivity<OTPVerificationActivity>(
                    OTPVerificationActivity.MOBILE_NUMBER to binding.etLoginMno.getTextString()
                )
                finish()
            }
        }
    }

    private fun clickListeners() {
        binding.run {
            btnLoginRequestOTP.setOnClickListener(this@LoginActivity)
            tvLoginRegister.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLoginRequestOTP -> validate()
            R.id.tvLoginRegister -> startActivity<RegistrationActivity>()
        }
    }

    private fun validate() {
        binding.run {
            when {
                etLoginMno.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                !etLoginMno.text.toString().isMobileNumberValid -> setShakeError(getString(R.string.please_valid_phone))
                pref.fcmToken.isNullOrEmpty() -> AppGlobal.getFcmToken(this@LoginActivity) { login() }
                else -> login()
            }
        }
    }

    private fun login() {
        viewModel.login(
            Login(
                binding.etLoginMno.getTextString(),
                "A",
                BuildConfig.VERSION_NAME,
                pref.fcmToken.orEmpty(),
                1
            )
        )
    }
}
