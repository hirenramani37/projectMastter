package com.common.data.network.model

data class OrderHistoryResponse(
    val current_page: Int,
    val data: List<OrderHistoryDetails>?,
    val first_page_url: String,
    val from: Int,
    val last_page: Int = 0,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int = 0,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)



