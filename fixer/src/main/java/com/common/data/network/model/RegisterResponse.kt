package com.common.data.network.model

data class RegisterResponse(
    val id: Int,
    val phone_no: String,
    val country_code: Int,
    val otp: Int,
    val api_token: String,
    val is_verify: Int,
    val created_at: String,
    val default_lang: String
)