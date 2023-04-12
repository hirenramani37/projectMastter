package com.common.data.network.model.request

data class CreateRegistration(
    val phone_no: String,
    val device_type: String,
    val full_name: String,
    val device_token: String,
    val current_version: String,
    val is_register: Int,
)
