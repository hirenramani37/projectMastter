package com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat

enum class ReadStatus(val status: Int) {
    PENDING(0),
    SENT(1),
    DELIVERED(2),
    READ(3),
    FAILED(4)
}