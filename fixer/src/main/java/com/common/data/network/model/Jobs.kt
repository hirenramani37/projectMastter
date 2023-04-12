package com.common.data.network.model

data class Jobs(
    val ar_status: String,
    val description: String,
    val fixer_id: Int,
    val id: Int,
    val is_interested: Int,
    val job_status_id: Int,
    val status: String,
    val title: String,
    val user_id: Int,
    val user_rating: Int
)