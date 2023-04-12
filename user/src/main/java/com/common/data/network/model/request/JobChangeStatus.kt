package com.common.data.network.model.request

data class JobChangeStatus(
    val job_id: String,
    val status: String,
    val reason_id: String,
    val description: String,
)
