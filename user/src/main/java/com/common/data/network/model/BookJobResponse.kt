package com.common.data.network.model

import java.io.Serializable

data class BookJobResponse(
    val admin_cancel_comm: Any,
    val admin_commission: Any,
    val appointment_date: String,
    val appointment_time: String,
    val approx_hour: Int,
    val ar_status: String,
    val banner_image: String? = "",
    val cancel_reason: String = "",
    val category_id: Int,
    val completed_task_datetime: String,
    val created_at: String,
    val description: String,
    val fixer_amount: Any,
    val fixer_id: String = "",
    val id: Int,
    val invoice_no: String = "",
    val is_interested: String = "",
    val job_image: ArrayList<JobImages> = arrayListOf(),
    val job_status_id: Int = 0,
    val location_latitude: String = "",
    val location_longitude: String = "",
    val location_name: String = "",
    val price: String = "",
    val price_type: Int,
    val reason_id: Int,
    val refund_amount: String = "",
    val required_equipment: String = "",
    val response_time: String = "",
    val skill_ids: String = "",
    val start_task_datetime: String = "",
    val status: String = "",
    val stripe_card_id: Int,
    val stripe_refund_id: Int,
    val stripe_transaction_id: String = "",
    val subcategory_ids: String = "",
    val title: String = "",
    val updated_at: String = "",
    val user_id: Int,
    val user_payment: String = "",
    val user_rating: Int,
)

data class JobImages(
    val id: Int,
    val image: String,
    val job_id: Int,
) : Serializable