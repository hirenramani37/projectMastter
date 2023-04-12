package com.common.data.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class ChatDummy(
    var message: String? = null,
    var time: String? = null,
)

@Entity
open class Chat : Serializable {
    @PrimaryKey
    var m_id: String = "" //Generated from UUID
    var viewType: Int? = 0
    var message: String? = ""
}

