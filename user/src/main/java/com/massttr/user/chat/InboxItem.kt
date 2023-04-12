package com.massttr.user.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InboxItem(
    val chat_id: String,
): Parcelable