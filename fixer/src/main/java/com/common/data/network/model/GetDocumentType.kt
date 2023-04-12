package com.common.data.network.model

data class GetDocumentType(
    val ar_name: String,
    val en_name: String,
    val id: Int,
    val max_doc: Int,
//    var documents: ArrayList<Document>? = null,
    var documents: ArrayList<Document> = arrayListOf(),
)