package com.common.data.network.model

data class RegistrationResponse(
    val default_lang: String,
    val id: Int,
    val is_registered: Int,
    val is_verify: Int,
    val mobile_no: String,
    val otp: Int,
    val token: String,
)