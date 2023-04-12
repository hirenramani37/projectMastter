package com.common.data.network.model

class NotificationData(
    val ar_message: String,
    val created_at: String,
    val fixer_id: Int,
    val id: Int,
    val job_id: Int,
    val jobs: Jobs,
    val message: String,
    val type: Int,
    val updated_at: String,
    val user_id: Any
)

