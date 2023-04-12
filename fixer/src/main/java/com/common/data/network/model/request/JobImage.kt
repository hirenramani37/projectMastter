package com.common.data.network.model.request

data class JobImage(
    val job_id: Int,
    val type: Int,
    val env: String = "test"
)
