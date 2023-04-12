package com.common.data.network.model

data class NotificationResponse(
   val id:Int,
   val user_id: Int,
   val fixer_id: Int,
   val job_id: Int,
   val type: Int,
   val ar_title: String,
   val title: String,
   val message: String,
   val ar_message: String,
   val created_at: String,
   val updated_at: String,
   var headerId: Long?,
   var headerValue: String?,
   val jobs: Jobs?,
)

