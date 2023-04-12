package com.common.data.network.model

data class FixerJobImage(
    val created_at: String,
    val id: Int,
    val image: String,
    val job_id: Int,
    val type: Int,
    val updated_at: String
)