package com.common.data.network.model

class CommonResponses<T>(
    val success: Boolean = false,
    val data: List<T>? = null,
    val message: String = "",
)