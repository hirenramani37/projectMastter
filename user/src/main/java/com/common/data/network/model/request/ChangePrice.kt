package com.common.data.network.model.request

data class ChangePrice(
    val price: String,
    val job_id: Int,
    val approx_hour: String,
)
