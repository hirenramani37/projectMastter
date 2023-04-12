package com.common.data.network.model

data class ContactUsResponse(
    val data: List<Any>,
    val message: String,
    val success: Boolean,
)