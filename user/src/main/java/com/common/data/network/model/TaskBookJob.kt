package com.common.data.network.model

import java.io.File
import java.io.Serializable

data class TaskBookJob(
    var bannerUrl: File,
    var title: String,
    var subCateArrayList: ArrayList<Subcategory>,
    var taskPhotos: ArrayList<File>,
    var taskDescription: String,
    var date: String,
    var time: String,
    var hourlyRate: String,
    var hours: String,
    var dkkPrice: String,
    var priceType: Int,
    var requiredTool: String,
    var address: String,
    var latitude: String,
    var longitude: String,
    val category_id: Int,
    val subcategory_ids: String,
) : Serializable