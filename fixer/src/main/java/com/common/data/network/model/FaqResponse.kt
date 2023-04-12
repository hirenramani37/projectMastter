package com.common.data.network.model

data class FaqResponse(
    val ar_answer: String,
    val ar_question: String,
    val en_answer: String,
    val en_question: String,
    val type: String
)
