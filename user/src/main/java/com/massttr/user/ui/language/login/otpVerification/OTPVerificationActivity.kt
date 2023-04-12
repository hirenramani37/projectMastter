package com.massttr.user.ui.language.login.otpVerification

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.common.base.BaseActivity
import com.common.data.network.model.request.OTPVerification
import com.massttr.user.R
import com.massttr.user.databinding.ActivityOtpVerificationBinding
import com.massttr.user.ui.language.login.otpVerification.verified.VerifyActivity
import com.massttr.user.ui.main.MainActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class OTPVerificationActivity :
    BaseActivity<ActivityOtpVerificationBinding>(R.layout.activity_otp_verification),
    View.OnClickListener {

    private val viewModel: OTPVerificationViewModel by viewModels()
    private var fullName: String = ""
    private var mobileNo: String = ""

    companion object {
        const val FULL_NAME = "FULL_NAME"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        startTimer()
        setUpObserver()
        clickListener()
    }

    private fun init() {
        if (intent.hasExtra(FULL_NAME))
            fullName = intent.getStringExtra(FULL_NAME).toString()
        if (intent.hasExtra(MOBILE_NUMBER))
            mobileNo = intent.getStringExtra(MOBILE_NUMBER).toString()

        val changed = "<font color='#FFCB31'>(${getString(R.string.change)})</font>"
        binding.tvNumber.text =
            ("${getString(R.string.please_enter_the_4_digit)} ${""} $mobileNo $changed").toHtml()
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

    private fun setUpObserver() {
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
                        finish()
                    } else {
                        startActivity<VerifyActivity>(
                            FULL_NAME to fullName,
                            MOBILE_NUMBER to mobileNo
                        )
                        finish()
                    }
                } else {
                    setShakeError(resp.message)
                }
            }

            viewModel.otpResend.observe(this@OTPVerificationActivity) { otp ->
                binding.pvOTP.clearComposingText()
                if (otp.success) {
                    binding.pvOTP.setLineColor(selectedColorYellow())
                    toast(otp.data.otp.toString())
                } else {
                    setShakeError(otp.message)
                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            btnVerify.setOnClickListener(this@OTPVerificationActivity)
            tvNumber.setOnClickListener(this@OTPVerificationActivity)
            tvResend.setOnClickListener(this@OTPVerificationActivity)
        }
    }

    private fun startTimer() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                binding.run {
                    tvVerificationAccount.visible()
                    tvResend.visible()
                }
            }
        }.start()
    }

    private fun verifyOTP() {
        binding.run {
            if (pvOTP.text.toString().length == 4)
                viewModel.otpVerification(OTPVerification(pvOTP.getTextString()))
            else
                setShakeError(getString(R.string.please_enter_OTP))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnVerify -> verifyOTP()
            R.id.tvNumber -> onBackPressed()
            R.id.tvResend -> viewModel.otpResend()
        }
    }
}