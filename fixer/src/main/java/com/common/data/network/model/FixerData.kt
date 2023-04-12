package com.common.data.network.model

data class FixerData(
    val appointment_date: String,
    val appointment_time: String,
    val ar_status: String,
    val banner_image: String,
    val completed_task_datetime: String,
    val description: String,
    val fixer_id: Int,
    val id: Int,
    val is_interested: Int,
    val job_image: List<FixerJobImage>,
    val job_status_id: Int,
    val price: String,
    val price_type: Int,
    val required_equipment: Any,
    val start_task_datetime: String,
    val status: String,
    val title: String,
    val user: User,
    val user_id: Int,
    val user_rating: Int
)