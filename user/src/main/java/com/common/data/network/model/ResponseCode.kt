package com.common.data.network.model

enum class ResponseCode constructor(val code: Int) {
    OK(200),
    Crated(201),
    BadRequest(400),
    Unauthenticated(401),
    Unauthorized(403),
    NotFound(404),
    RequestTimeOut(408),
    Conflict(409),
    InvalidData(422),
    Blocked(423),
    ForceUpdate(426),
    InternalServerError(500)
}

data class BaseResponse<T>(
    var success: Boolean = false,
    var message: String = "",
    var data: T? = null
)