package com.common.data.network.model.request

data class Login(
    val mobile_no: String,
    val device_type: String,
    val current_version: String,
    val device_token: String,
    val is_login: Int,
)
