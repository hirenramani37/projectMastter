package com.common.data.network.model

data class Earning(
    val admin_commission: String,
    val admin_commission_percentage: Any,
    val created_at: String,
    val deleted_at: Any,
    val final_price: String,
    val fixer_id: Int,
    val id: Int,
    val job: JobEarning?,
    val job_id: Int,
    val job_price: String,
    val updated_at: String
)
