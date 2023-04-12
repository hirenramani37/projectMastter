package com.massttr.provider.ui.language.login.register.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.common.base.BaseViewModel
import com.common.base.SingleLiveEvent
import com.common.data.network.model.CommonResponse
import com.common.data.network.model.OTPResendResponse
import com.common.data.network.model.OTPVerificationResponse
import com.common.data.network.model.UserInfo
import com.common.data.network.model.request.OTPVerification
import kotlinx.coroutines.launch

class OTPVerificationViewModel : BaseViewModel() {
    private val _otpVerification = SingleLiveEvent<CommonResponse<UserInfo>>()
    val otpVerification: LiveData<CommonResponse<UserInfo>> = _otpVerification

    private val _otpResend = SingleLiveEvent<OTPResendResponse>()
    val otpResend: LiveData<OTPResendResponse> = _otpResend

    fun otpVerification(otp: OTPVerification) {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.otpVerification(otp)) {
                _otpVerification.postValue(it)
            }
        }
    }

    fun otpResend() {
        viewModelScope.launch {
            displayLoader()
            processDataEvent(api1Repository.resendOTP()) {
                _otpResend.postValue(it)
            }
        }
    }
}