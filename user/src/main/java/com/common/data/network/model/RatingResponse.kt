package com.common.data.network.model

data class RatingResponse(
    val created_at: String,
    val fixer_id: String,
    val id: Int,
    val job_id: String,
    val rate: String,
    val review: String,
    val updated_at: String,
    val user_id: Int,
)