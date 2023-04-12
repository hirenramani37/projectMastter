package com.common.data.network.model

data class EarningResponse(
    val earning: String,
    val earning_list: List<Earning>,
    val total_admin_commission: Double,
    val total_earning: Double
)


data class JobEarning(
    val ar_status: String?,
    val id: Int?,
    val is_interested: Int?,
    val status: String?,
    val title: String?,
    val user_rating: Int?
)