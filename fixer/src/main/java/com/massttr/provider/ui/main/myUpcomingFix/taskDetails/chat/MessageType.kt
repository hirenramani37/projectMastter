package com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat

enum class MessageType(val type: Int) {
    NONE(0),
    TEXT(1),
    PHOTO(2),
    VIDEO(3),
    CHANGE_PRICE(4),

    TEXT_SENDER(101_1),
    IMAGE_SENDER(102_1),
    VIDEO_SENDER(103_1),

    TEXT_RECEIVER(101_2),
    IMAGE_RECEIVER(102_2),
    VIDEO_RECEIVER(103_2),
    CHANGE_PRICE_RECEIVER(104_2),
}
