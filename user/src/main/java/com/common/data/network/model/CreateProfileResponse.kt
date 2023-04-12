package com.common.data.network.model

data class CreateProfileResponse(
    val data: UserInfo?,
    val message: String,
    val success: Boolean,
)