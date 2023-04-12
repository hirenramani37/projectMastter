package com.massttr.provider.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Online(
    val type: Int,
    val status: Int,
    val user_id: Int
): Parcelable