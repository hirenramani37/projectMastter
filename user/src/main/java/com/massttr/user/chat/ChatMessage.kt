package com.massttr.user.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessage(
    val chat_id: String,
    val created_date: String,
    val id: Int,
    val m_id: String,
    val message: String,
    val message_type: Int,
    var read_status: Int,
    val receiver_id: Int,
    val sender_id: Int,
    val sender_name: String,
    val sender_avatar: String,
    val job_id: Int,
    val count: String?,


    var headerId: Long?,
    var headerValue: String?,
) : Parcelable