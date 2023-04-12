package com.massttr.provider.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val chat_id: String,
    val m_id: String,
): Parcelable