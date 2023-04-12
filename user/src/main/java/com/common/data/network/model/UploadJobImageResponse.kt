package com.common.data.network.model

import java.io.Serializable

data class UploadJobImageResponse(
    val success: Boolean,
    val data: List<String>,
    val message: String,
) : Serializable


