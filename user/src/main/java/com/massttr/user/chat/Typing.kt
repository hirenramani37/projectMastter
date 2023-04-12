package com.massttr.user.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Typing(
    val type: Int,
    val typing: Int,
    val chat_id: String,
    val userId: Int
): Parcelable