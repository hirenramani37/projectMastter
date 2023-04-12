package com.massttr.provider.ui.language.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.common.base.BaseActivity
import com.common.data.network.model.request.Login
import com.google.android.gms.maps.model.Circle
import com.massttr.user.utils.*
import com.massttr.provider.BuildConfig
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityLoginBinding
import com.massttr.provider.ui.language.login.register.RegisterActivity
import com.massttr.provider.ui.language.login.register.verify.OTPVerificationActivity
import kotlinx.coroutines.DefaultExecutor.delay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity


@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login),
    View.OnClickListener {

    private val viewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGlobal.getFcmToken(this) {}
        setUpObserver()
        clickListener()

        //delay(1000L)

//        fun Circle.perimeter(): Double{
//            return 2 * Math.PI * radius;
//        }
//
//        fun main(){
//
//            val circle = Circle(5.5);
//            val perimeterValue = circle.perimeter()
//            println("Perimeter: $perimeterValue")
//            val areaValue = circle.area()
//            println("Area: $areaValue")
//        }
    }

    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(this@LoginActivity) { handleError(it) }
            appLoader.observe(this@LoginActivity) { updateLoaderUI(it) }
            login.observe(this@LoginActivity) {
                pref.authToken = it.data?.api_token
                pref.accountCreatedDate = it.data?.created_at
                startActivity<OTPVerificationActivity>(
                    OTPVerificationActivity.MOBILE_NUMBER to binding.etMno.getTextString(),
                    OTPVerificationActivity.OTP to it.data?.otp.toString()
                )
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnRequestOTP.setOnClickListener(this@LoginActivity)
            tvRegister.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRequestOTP -> validate()
            R.id.tvRegister -> startActivity<RegisterActivity>()
        }
    }

    private fun validate() {
        binding.run {
            when {
                etMno.isBlank() -> setShakeError(getString(R.string.please_enter_number))
                !etMno.text.toString().isMobileNumberValid -> setShakeError(getString(R.string.enter_valid_mobile_number))
                pref.fcmToken.isNullOrEmpty() -> {
                    updateLoaderUI(true)
                    AppGlobal.getFcmToken(this@LoginActivity) {
                        updateLoaderUI(false)
                        login()
                    }
                }
                else -> login()
            }
        }
    }

    private fun login() {
        viewModel.login(
            Login(
                binding.etMno.getTextString(),
                "A",
                pref.fcmToken.orEmpty(),
                BuildConfig.VERSION_NAME,
                1
            )
        )
    }
}

