package com.common.data.network.model

import java.io.Serializable

data class User(
    val address: String?,
    val chat_id: String?,
    val first_name: String?,
    val full_name: String?,
    val id: Int?,
    val last_name: String?,
    val profile_picture: String?,
    val zipcode: String?
) : Serializable
