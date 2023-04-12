package com.common.data.network.model

data class Document(
    val id: Int,
    val fixer_id: Int,
    val name: String,
    val document: String,
    val document_back: String,
    val type: Int,
    val is_verify: Int,
)