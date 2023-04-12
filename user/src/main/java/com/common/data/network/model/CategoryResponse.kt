package com.common.data.network.model

import java.io.Serializable

data class CategoryResponse(
    val id: Int,
    val ar_name: String,
    val en_name: String,
    val icon: String,
    val subcategory: List<Subcategory>?,
)

data class Subcategory(
    val id: Int? = 0,
    val category_id: Int = 0,
    val ar_name: String = "",
    val en_name: String = "",
    val icon: String = "",
    var isSelected: Boolean = false,
) : Serializable