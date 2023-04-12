package com.common.data.network.model.request

data class BookJob(
    val location_name: String,
    val location_latitude: String,
    val location_longitude: String,
    val title: String,
    val description: String,
    val category_id: Int,
    val subcategory_ids: String,
    val price_type: String,
    val price: String,
    val appointment_date: String,
    val appointment_time: String,
    val approx_hour: String,
    val required_equipment: String,
    val image: String,
    val banner_image: String,
)
