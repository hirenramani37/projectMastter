package com.common.data.network.model

import java.io.Serializable
data class FaqResponse(
    val data: List<Faq>?,
    val message: String,
    val success: Boolean,
)

data class Faq(
    val ar_answer: String,
    val ar_question: String,
    val en_answer: String,
    val en_question: String,
    val type: String,
) : Serializable