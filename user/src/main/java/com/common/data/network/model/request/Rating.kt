package com.common.data.network.model.request

data class Rating(
    val fixer_id: Int,
    val job_id: Int,
    val rate: Int,
    val review: String,
)
