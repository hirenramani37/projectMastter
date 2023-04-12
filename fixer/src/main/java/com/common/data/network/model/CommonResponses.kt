package com.common.data.network.model

class CommonResponses<T>(
    val status: Boolean = false,
    val message: String = "",
    val data: List<T> = listOf()
)
