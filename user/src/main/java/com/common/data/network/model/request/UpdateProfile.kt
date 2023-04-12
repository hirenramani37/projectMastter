package com.common.data.network.model.request

data class UpdateProfile(
    val full_name: String,
    val country_code: Int,
    val mobile_no: String,
    val zipcode: String,
    val address: String,
    val profile_picture: String,
    val company_name: String,
    val company_vat_no: String,
    val company_email: String,
    val is_send_invoice: Int,
    val gender: Int,
    val dob: String,
    val email: String,
    val lat: Double,
    val long: Double,
)
