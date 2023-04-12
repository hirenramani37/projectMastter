package com.common.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskList(
    val admin_cancel_comm: String,
    val admin_commission: String,
    val appointment_date: String,
    val appointment_time: String,
    val approx_hour: Int,
    val ar_status: String,
    val banner_image: String,
    val cancel_reason: String,
    val category_id: Int,
    val completed_task_datetime: String,
    val created_at: String,
    val description: String,
    val distance: Double,
    val fixer_amount: String,
    val fixer_id: String,
    val id: String,
    val invoice_no: String,
    val is_interested: Int,
    val job_image: List<JobImage>,
    val job_status_id: Int,
    val location_latitude: String,
    val location_longitude: String,
    val location_name: String,
    val price: String,
    val price_type: Int,
    val reason_id: String,
    val refund_amount: String,
    val required_equipment: String,
    val response_time: String,
    val skill_ids: String,
    val start_task_datetime: String,
    val status: String,
    val stripe_card_id: String,
    val stripe_refund_id: String,
    val stripe_transaction_id: String,
    val subcategory_ids: String,
    val title: String,
    val updated_at: String,
    val user_id: Int,
    val user_payment: String,
    val user_rating: Int
) : Parcelable
