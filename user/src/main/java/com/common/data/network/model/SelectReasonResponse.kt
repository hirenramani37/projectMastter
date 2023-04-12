package com.common.data.network.model

data class SelectReasonResponse(
    val data: List<SelectReasonData>,
    val message: String,
    val success: Boolean,
)

data class SelectReasonData(
    val ar_reason: String,
    val en_reason: String,
    val id: Int,
)