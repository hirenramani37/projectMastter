package com.common.data.network.model

data class OrderHistoryDetails(
    val appointment_date: String,
    val appointment_time: String,
    val approx_hour: Int,
    val ar_status: String,
    val banner_image: String,
    val description: String,
    val fixer_id: Int,
    val id: Int,
    val is_interested: String = "",
    val job_status_id: Int,
    //val near_by_fixer: List<NearByFixer>,
    var price: String,
    val price_type: Int,
    val receipt_link: String,
    val status: String,
    val title: String,
    val user_id: Int,
    val user_rating: Int
)

