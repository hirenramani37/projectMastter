package com.common.data.network.model

data class LogoutResponse(
    val data: List<Any>,
    val message: String,
    val success: Boolean
)