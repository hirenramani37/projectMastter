package com.common.data.network.model

data class SettingsResponse(
    val admin_email: String,
    val android_fixer_version: String,
    val android_force_update: String,
    val android_user_version: String,
    val cancellation_charge: String,
    val contact_email: String,
    val contact_no: String,
    val customer_support_email: String,
    val fixer_commission: String,
    val fixer_location_range: String,
    val fixer_support_email: String,
    val ios_fixer_version: String,
    val ios_force_update: String,
    val ios_user_version: String,
    val site_logo: String,
    val site_name: String
)