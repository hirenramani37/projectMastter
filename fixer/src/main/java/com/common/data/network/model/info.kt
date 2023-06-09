package com.common.data.network.model

data class info(
    val active_jobs: Int,
    val address: String,
    val admin_verify: Int,
    val avg_response_time: String,
    val birth_date: Any,
    val cities: Cities,
    val city: Int,
    val company_address: Any,
    val company_name: Any,
    val company_vat_no: Any,
    val company_zipcode: Any,
    val country_code: Int,
    val default_lang: String,
    val description: Any,
    val email_id: Any,
    val first_name: String,
    val gender: Int,
    val id: Int,
    val is_online: Int,
    val last_name: String,
    val location_latitude: Any,
    val location_longitude: Any,
    val phone_no: String,
    val profile_picture: String,
    val services: List<Any>,
    val skill_ids: Any,
    val state: Int,
    val states: States,
    val stripe_status: Int,
    val zipcode: String
)