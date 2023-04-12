package com.common.data.network.model.request

class JobStatus(
    val job_id: String,
    val status: String
)

data class JobChangeStatus(
    val job_id: Int,
    val status: Int,
    val reason_id: Int,
    val description: String,
)