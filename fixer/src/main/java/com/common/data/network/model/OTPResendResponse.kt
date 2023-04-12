package com.common.data.network.model

data class OTPResendResponse
    (
    val success: Boolean,
    val message: String,
    val data: OTPResend
)

data class OTPResend(val otp: String)