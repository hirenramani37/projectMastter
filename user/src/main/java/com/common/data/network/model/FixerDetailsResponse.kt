package com.common.data.network.model

import java.io.Serializable

data class FixerDetailsResponse(
    val fixer: Fixers,
) : Serializable


data class Fixers(
    val avg_rating: Float,
    val avg_response_time: String,
    val chat_id: String,
    val default_lang: String,
    val description: String,
    val document: List<Document>,
    val first_name: Any,
    val id: Int,
    val last_name: Any,
    val full_name: String,
    val location_latitude: String,
    val location_longitude: String,
    val phone_no: String,
    val resume: String,
    val nearbyfixer: Nearbyfixer,
    val past_fixes: List<PastFixes>,
    val profile_picture: String,
    val ratings: List<Any>,
    val ratings_count: Int,
    val total_completed_jobs: Int,
): Serializable

data class PastFixes(
    val id: Int,
    val fixer_id: Int,
    val banner_image: String,
    val status: String,
    val ar_status: String,
    val is_interested: Int,
    val user_rating: Int,
): Serializable

data class Document(
    val fixer_id: Int,
    val id: Int,
    val name: String,
): Serializable

data class Nearbyfixer(
    val distance: String,
    val fixer_id: Int,
    val job_id: Int,
): Serializable