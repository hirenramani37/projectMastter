package com.common.data.network.model.request

data class CreateProfile(
    val default_lang: String,
    val full_name: String,
    val country_code: String,
    val mobile_no: String,
    val dob: String,
    val email: String,
    val gender: Int,
    val zipcode: String,
    val address: String,
    val device_token: String,
    val profile_picture: String,
    val device_type: String,
    val lat: String,
    val long: String,
)
