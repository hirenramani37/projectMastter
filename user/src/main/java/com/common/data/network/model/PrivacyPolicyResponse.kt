package com.common.data.network.model

data class PrivacyPolicyResponse(
    val data: List<PrivacyPolicy>?,
    val message: String,
    val success: Boolean,
)

data class PrivacyPolicy(
    val ar_content: String,
    val ar_name: String,
    val created_at: String,
    val en_content: String,
    val en_name: String,
    val icon: String,
    val id: Int,
    val model_name: String,
    val status: String,
    val updated_at: String,
)