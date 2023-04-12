package com.common.data.network.model

data class DocumentData(
    val headerTitle: String,
    val type: Int,
    val max_doc: Int,
    val noDocFoundMsg: String,
    val documentList: ArrayList<Document>
)