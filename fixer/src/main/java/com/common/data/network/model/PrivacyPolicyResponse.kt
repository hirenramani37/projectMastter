package com.common.data.network.model

import java.io.Serializable

data class PrivacyPolicyResponse(
    val ar_content: String,
    val ar_name: String,
    val created_at: String,
    val en_content: String,
    val en_name: String,
    val icon: String,
    val id: Int,
    val inpage_image: String,
    val model_name: String,
    val status: String,
    val updated_at: String
) : Serializable