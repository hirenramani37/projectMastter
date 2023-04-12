package com.common.data.network.model.request

class UserLogin(
    val username: String,
    val password: String,
    val user_type: String = "business"
)
