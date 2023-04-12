package com.common.data.network.model

import android.os.Parcelable
import java.io.Serializable

class CompleteJob(
    val total_jobs: Int?,
    val jobs: CompleteJobPageData?
):Serializable


data class CompleteJobPageData(
    var current_page: Int? = 0,
    var data: List<CompleteJobMainData> = arrayListOf(),
    var first_page_url: String? = "",
    var from: Int? = 0,
    var last_page: Int? = 0,
    var last_page_url: String? = "",
    var next_page_url: String? = "",
    var path: String? = "",
    var per_page: Int? = 0,
    var prev_page_url: String? = "",
    var to: Int? = 0,
    var total: Int? = 0
) : Serializable


data class CompleteJobMainData(
    var appointment_date: String? = "",
    var appointment_time: String? = "",
    var approx_hour: Int? = 0,
    var banner_image: String? = "",
    var cancel_reason: String? = "",
    var completed_task_datetime: String? = "",
    var created_at: String? = "",
    var da_status: String? = "",
    var description: String? = "",
    var distance: Double? = 0.0,
    var fixer_id: String? = "",
    var id: Int? = 0,
    var invoice_no: String? = "",
    var is_interested: Int? = 0,
    var job_status_id: Int? = 0,
    var location_latitude: String? = "",
    var location_longitude: String? = "",
    var location_name: String? = "",
    var price: String? = "",
    var price_type: Int? = 0,
    var reason_id: String? = "",
    var required_equipment: String? = "",
    var skill_ids: String? = "",
    var start_task_datetime: String? = "",
    var status: String? = "",
    var title: String? = "",
    var updated_at: String? = "",
    var user_id: Int? = 0,
    var user : User?,
    var user_payment: Float?,//use for completeJob list
    val receipt_link: String?,//use for completeJob list
    val user_rating: Float?//use for completeJob list
) : Serializable
