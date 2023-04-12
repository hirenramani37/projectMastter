package com.common.data.network.model.request

data class FixerRegistration(
    val default_lang: String,
    val full_name: String,
    val country_code: Int,
    val phone_no: String,
    val email_id: String,
    val birth_date: String,
    val gender: Int,
    val zipcode: String,
    val address: String,
    val city: Int,
    val state: Int,
    val company_name: String,
    val company_vat_no: String,
    val company_zipcode: String,
    val company_email: String,
    val company_address: String,
    val subcategory_ids: String,
    val comments: String,
    val device_token: String,
    val is_send_invoice: Int,
    val profile_picture: String,
    val city_name: String,
    val state_name: String
)