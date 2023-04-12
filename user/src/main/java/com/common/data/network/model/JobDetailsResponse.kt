package com.common.data.network.model

import java.io.Serializable

data class JobDetailsResponse(
    val card: Any,
    val equipments: List<String>,
    val job: Job,
    val receipt_link: String,
)

data class Job(
    val appointment_date: String,
    val appointment_time: String,
    val approx_hour: Int,
    val ar_status: String,
    val banner_image: String,
    val description: String,
    val fixer: Fixers,
    val fixer_id: Int,
    val id: Int,
    val is_interested: Any,
    val job_image: List<JobImage>,
    val job_status_id: Int,
    val price: String,
    val price_type: Int,
    val reason: Any,
    val receipt_link: String,
    val required_equipment: Any,
    val status: String,
    val title: String,
    val user_id: Int,
    val user_payment: String,
    val user_rating: Int,
)

data class JobImage(
    val image: String,
    val job_id: Int,
    val type: Int,
) : Serializable