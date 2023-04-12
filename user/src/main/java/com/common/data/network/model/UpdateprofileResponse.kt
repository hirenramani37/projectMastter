package com.common.data.network.model

data class UpdateProfileResponse(
    val id: Int = 0,
    val profile_picture: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    val email_verified_at: Int = 0,
    val country_code: String = "",
    val mobile_no: String = "",
    val dob: String = "",
    val chat_password: String = "",
    val gender: Int = 0,
    val zipcode: String = "",
    val address: String? = null,
    val company_name: String = "",
    val company_vat_no: String = "",
    val company_email: String = "",
    val is_send_invoice: Int = 0,
    val otp: String = "",
    val social_id: Any,
    val social_type: Any,
    val default_lang: String = "",
    val is_active: Int = 0,
    val is_registered: Int = 0,
    val is_verify: Int = 0,
    val device_token: String = "",
    val device_type: String = "",
    val current_version: String = "",
    val fb_social_id: Any,
    val google_social_id: Any,
    val apple_id: Any,
    val admin_comment: Any,
    val user_stripe_id: Any,
    val created_at: String = "",
    val updated_at: String = "",
    val deleted_at: String = "",
    val stripe_id: String = "",
    val card_brand: String = "",
    val card_last_four: String = "",
    val trial_ends_at: String = "",
    val full_name: String = "",
    val chat_id: String = "",
)


