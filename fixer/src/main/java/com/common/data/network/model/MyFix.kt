package com.common.data.network.model

import java.io.Serializable

data class MyFix(
    var current_page: Int? = 0,
    var data: List<Job>? = listOf(),
    var first_page_url: String? = "",
    var from: Int? = 0,
    var last_page: Int?=0,
    var last_page_url: String? = "",
    var next_page_url: String? = "",
    var path: String? = "",
    var per_page: Int? = 0,
    var prev_page_url: String? = "",
    var to: Int? = 0,
    var total: Int? = 0
) :Serializable


data class Job(
    var appointment_date: String? = "",
    var appointment_time: String? = "",
    var approx_hour: Int? = 0,
    var banner_image: String? = "",
    var cancel_reason: String? = "",
    var completed_task_datetime: String? = "",
    var created_at: String? = "",
    var ar_status: String? = "",
    var description: String? = "",
    var fixer: Fixer? = null,
    var fixer_id: Int? = 0,
    var id: Int? = 0,
    var invoice_no: String? = "",
    var is_interested: Int? = 0,
    var job_image: List<JobImage>? = arrayListOf(),
    var job_status_id: Int? = 0,
    var location_latitude: String? = "",
    var location_longitude: String? = "",
    var location_name: String? = "",
    var price: String? = "",
    var price_type: Int? = 0,
    var reason_id: Int? = 0,
    var required_equipment: String? = "",
    var skill_ids: String? = "-",
    var start_task_datetime: String? = "",
    var status: String? = "",
    var title: String? = "",
    var updated_at: String? = "",
    var user: User,
    var user_id: Int? = 0
) : Serializable

data class Fixer(
    var avg_rating: Int? = 0,
    var chat_id: Int? = 0,
    var default_lang: String? = "",
    var first_name: String? = "",
    var full_name: String? = "",
    var id: Int? = 0,
    var last_name: String? = "",
    var profile_picture: String? = "",
    //  var ratings: List<Any>,
    var total_completed_jobs: Int? = 0
) : Serializable
