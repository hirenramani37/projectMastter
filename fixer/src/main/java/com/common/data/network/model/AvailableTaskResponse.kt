package com.common.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class AvailableTaskResponse(
    val current_page: Int,
    val data: List<TaskList>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

@Parcelize
data class JobImage(
    val id: Int,
    val job_id: Int,
    val image: String,
    val type: Int,
    val created_at: String,
    val updated_at: String
) : Parcelable
