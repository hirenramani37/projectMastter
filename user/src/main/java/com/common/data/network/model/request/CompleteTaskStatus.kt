package com.common.data.network.model.request

data class CompleteTaskStatus(
    val job_id: String,
    val status: String,
    val fixer_id: String,
)
