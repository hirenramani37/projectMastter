package com.common.data.network.model

data class OTPResendResponse(
    val data: ResendOTP,
    val message: String,
    val success: Boolean,
)

data class ResendOTP(
    val otp: Int,
)