package com.massttr.user.ui.main.inbox.chat

enum class ReadStatus(val status: Int) {
    PENDING(0),
    SENT(1),
    DELIVERED(2),
    READ(3),
    FAILED(4)
}