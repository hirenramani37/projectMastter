package com.massttr.provider.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Inbox(
    val chat_id: String,
    val chat_user_avatar: String?,
    val chat_user_id: Int,
    val chat_user_name: String,
    var is_online: Boolean,
    val job_description: String,
    val job_id: Int,
    val job_location: String,
    val job_status_id: Int,
    val chat_user_mobile_no: String,
    val job_price: Double,
    val job_title: String,
    var last_message: String,
    var last_message_date: String,
    val login_user_id: Int,
    val type: String,
    var unread_count: Int
) : Parcelable