package com.common.data.network.model

data class NotificationResponse(
    val ar_message: String,
    val created_at: String,
    val fixer_id: Any,
    val id: Int,
    val job_id: Int,
    val jobs: Jobs,
    val message: String,
    val type: Int,
    val is_read : Int,
    val title: String,
    val ar_title: String,
    val updated_at: String,
    var headerId: Long?,
    var headerValue: String?,
    val user_id: Int,
)

data class Jobs(
    val ar_status: Any,
    val description: String,
    val id: Int,
    val is_interested: Any,
    val status: Any,
    val title: String,
    val user_rating: Int,
)