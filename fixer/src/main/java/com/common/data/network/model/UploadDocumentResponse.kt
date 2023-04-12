package com.common.data.network.model

data class UploadDocumentResponse(
    val created_at: String,
    val document: String,
    val fixer_id: Int,
    val id: Int,
    val name: String,
    val type: String,
    val updated_at: String
)