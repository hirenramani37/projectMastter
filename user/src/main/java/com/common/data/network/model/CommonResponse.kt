package com.common.data.network.model

class CommonResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val message: String = "",
)