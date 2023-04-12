package com.common.data.network.model

import java.io.Serializable

data class NearBy(
    val current_page: Int,
    val data: List<NearByFixersData>? = null,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int,
)

data class NearByFixersData(
    val distance: String,
    val fixer: Fixer,
    val fixer_id: Int,
    val id: Int,
    val is_interested: Int,
    val job_id: Int,
)

data class Fixer(
    val avg_rating: Float,
    val avg_response_time: String = "",
    val chat_id: String = "",
    val default_lang: String = "",
    val description: String = "",
    val first_name: String = "",
    val full_name: String = "",
    val id: Int = 0,
    val last_name: String = "",
    val profile_picture: String = "",
    val ratings_count: Int = 0,
    val total_completed_jobs: Int = 0,
) : Serializable
