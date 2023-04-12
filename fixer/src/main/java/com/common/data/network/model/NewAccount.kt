package com.common.data.network.model

data class NewAccount(
    val api_token: String,
    val default_lang: String,
    val id: Int,
    val is_verify: Int,
    val otp: Int,
    val phone_no: String
)