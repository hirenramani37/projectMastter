package com.common.data.network.model.request

data class  Registration(
    val full_name: String,
    val mobile_no: String,
    val device_type: String,
    val device_token: String,
    val current_version: String,
    val is_register: Int,
)
