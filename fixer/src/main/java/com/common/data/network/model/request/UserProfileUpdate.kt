package com.common.data.network.model.request

class UserProfileUpdate(
    val profile_picture: String,
    val full_name: String,
    val country_code: String,
    val phone_no: String,
    val email_id: String,
    val birth_date: String,
    val zipcode: String,
    val address: String,
    val company_name: String,
    val company_vat_no: String,
    val company_email: String,
    val company_zipcode: String,
    val company_address: String,
    val is_send_invoice: String,
    val state: String,
    val city: String,
    val env: String,
    val gender: String,
    val city_name: String,
    val state_name: String,
)