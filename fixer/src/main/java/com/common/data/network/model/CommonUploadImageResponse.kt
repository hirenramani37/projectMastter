package com.common.data.network.model

data class CommonUploadImageResponse(
    val data: List<String>,
    val message: String,
    val success: Boolean,
)