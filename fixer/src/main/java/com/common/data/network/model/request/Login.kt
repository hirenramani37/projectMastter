package com.common.data.network.model.request

data class Login(
    val phone_no: String,
    val device_type: String,
    val device_token: String,
    val current_version: String,
    val is_login: Int,
)
