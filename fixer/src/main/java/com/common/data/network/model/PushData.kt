package com.common.data.network.model

data class PushData(
    val message: String,
    val updatedAt: String,
    val type: String,
    val title: String,
    val data: String
)

//data class PushData(
//    val job_id: Int
//)