package com.common.data.network.model

data class OTPVerificationResponse(
    val data: UserInfo,
)

/*data class UserData(
    val address: String,
    val admin_verify: Int,
    val api_token: String,
    val avg_rating: Int,
    val avg_response_time: String,
    val birth_date: String,
    val chat_id: String,
    val chat_password: String,
    val company_address: String,
    val company_email: String,
    val company_name: String,
    val company_vat_no: String,
    val company_zipcode: String,
    val country_code: Int,
    val current_version: String,
    val default_lang: String,
    val description: String,
    val device_token: String,
    val email_id: String,
    val first_name: String,
    val full_name: String,
    val gender: Int,
    val id: Int,
    val is_active: Int,
    val is_online: Int,
    val is_registered: Int,
    val is_verify: Int,
    val last_name: String,
    val location_latitude: Any,
    val phone_no: String,
    val profile_picture: String,
    val ratings_count: Int,
    val services: List<Service>,
    val skill_ids: String,
    val total_completed_jobs: Int,
    val zipcode: String,
)*/
