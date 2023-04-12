package com.massttr.provider.ui.language.login.register.verify

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.common.base.BaseActivity
import com.common.data.network.model.request.OTPVerification
import com.massttr.provider.R
import com.massttr.provider.databinding.ActivityOtpverificationBinding
import com.massttr.provider.ui.main.MainActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class OTPVerificationActivity :
    BaseActivity<ActivityOtpverificationBinding>(R.layout.activity_otpverification),
    View.OnClickListener {
    private val viewModel: OTPVerificationViewModel by viewModels()
    var fullName: String = ""
    private var mobileNo: String = ""

    companion object {
        const val FULL_NAME = "FULL_NAME"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
        const val OTP = "OTP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setObserver()
        clickListener()
    }

    private fun init() {
        if (intent.hasExtra(FULL_NAME)) fullName = intent.getStringExtra(FULL_NAME).toString()
        if (intent.hasExtra(MOBILE_NUMBER)) {
            mobileNo = intent.getStringExtra(MOBILE_NUMBER).toString()
            val first = getString(R.string.please_enter_the_4_digit)
            val changed = getString(R.string.change)
            binding.tvNumber.text = Html.fromHtml("$first $mobileNo $changed")
        }
        if (intent.hasExtra(OTP)) setShakeErrorInternet(intent.getStringExtra(OTP).toString())
    }

    private fun selectedColor(
        @ColorInt color: Int = ContextCompat.getColor(
            this,
            R.color.colorToastError
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    private fun selectedColorYellow(
        @ColorInt color: Int = ContextCompat.getColor(
            this,
            R.color.colorAccent
        ),
    ): ColorStateList {
        return ColorStateList.valueOf(color)
    }

    private fun setObserver() {
        viewModel.run {
            apiErrors.observe(this@OTPVerificationActivity) {
                handleError(it)
                binding.pvOTP.setLineColor(selectedColor())
            }
            appLoader.observe(this@OTPVerificationActivity) { updateLoaderUI(it) }
            otpVerification.observe(this@OTPVerificationActivity) { resp ->
                if (resp.data?.is_verify == 1) {
                    pref.userInfo = resp.data
                    if (resp.data.is_registered == 1) {
                        pref.isLogin = true
                        startSocketService()
                        startActivity<MainActivity>()
                        finishAffinity()
                    } else {
                        startActivity<VerifyActivity>(
                            FULL_NAME to fullName,
                            MOBILE_NUMBER to mobileNo
                        )
                        finishAffinity()
                    }
                } else setShakeError(resp.message)
            }
            viewModel.otpResend.observe(this@OTPVerificationActivity) { otp ->
                if (otp.success) {
                    binding.pvOTP.setLineColor(selectedColorYellow())
                    setShakeErrorInternet(otp.data.otp)
                } else setShakeError(otp.message)
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnVerify.setOnClickListener(this@OTPVerificationActivity)
            tvResend.setOnClickListener(this@OTPVerificationActivity)
            tvNumber.setOnClickListener(this@OTPVerificationActivity)
        }
    }

    private fun verifyOTP() {
        if (binding.pvOTP.text.toString().length == 4)
            viewModel.otpVerification(OTPVerification(binding.pvOTP.text.toString()))
        else
            setShakeError(getString(R.string.please_enter_OTP))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVerify -> verifyOTP()
            R.id.tvNumber -> onBackPressed()
            R.id.tvResend -> {
                binding.pvOTP.clearComposingText()
                viewModel.otpResend()
            }
        }
    }
}
