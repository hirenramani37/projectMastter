package com.common.data.network.model

import java.io.Serializable

data class Subcategory(
    val ar_name: String,
    val category_id: Int,
    val en_name: String,
    val icon: String,
    val id: Int,
    var isSelected: Boolean = false
) : Serializable