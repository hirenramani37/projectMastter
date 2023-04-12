package com.common.data.network.model

data class HomeMapGetFixerResponse(
    val avg_rating: Float,
    val avg_response_time: String,
    val phone_no: String,
    val chat_id: String,
    val distance: Double,
    val full_name: String,
    val id: Int,
    val location_latitude: Double,
    val location_longitude: Double,
    val profile_picture: String,
    val ratings_count: Int,
    val total_completed_jobs: Int
)