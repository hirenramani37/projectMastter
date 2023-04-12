package com.common.data.network.model

data class LanguageResponse(
    val data: List<Any>,
    val message: String,
    val success: Boolean
)