package com.massttr.user.utils

/**
 * Created by Hiren on 22-9-2021.
 */


//const val DOMAIN = ""   //  live chat
const val DOMAIN = ""  //  testing chat

const val API_BASE_URL = "https://massttr.elaunchinfotech.com/api/v1/" // live
const val SOCKET_HOST = "http://$DOMAIN:3000"

//const val SERVER_DOMAIN = ""  // Live Server
const val SERVER_DOMAIN = ""  // Testing Server

const val FORMAT_MESSAGE_TIME = "h:mm a"

//Day Name in english and arabic
const val EN_SUNDAY = "Sunday"
const val AR_SUNDAY = "أحد"

const val EN_MONDAY = "Monday"
const val AR_MONDAY = "أثنين"

const val EN_TUESDAY = "Tuesday"
const val AR_TUESDAY = "ثلاثاء"

const val EN_WEDNESDAY = "Wednesday"
const val AR_WEDNESDAY = "أربعاء"

const val EN_THURSDAY = "Thursday"
const val AR_THURSDAY = "خميس"

const val EN_FRIDAY = "Friday"
const val AR_FRIDAY = "جمعة"

const val EN_SATURDAY = "Saturday"
const val AR_SATURDAY = "جمعة"

var CURRENT_OTHER_USER_ID = -1

/* region Events*/
const val BUS_EVENT_MANUALLY_LAST_MESSAGE = "EVENT_INBOX_LIST"

const val BUS_EVENT_CREATE_CHAT = "EVENT_CREATE_CHAT"
const val BUS_EVENT_INBOX_LIST = "EVENT_INBOX_LIST"
const val BUS_EVENT_ONLINE = "EVENT_ONLINE"
const val BUS_EVENT_TYPING = "EVENT_TYPING"
const val BUS_EVENT_MESSAGE_RECEIVED = "EVENT_MESSAGE_RECEIVED"
const val BUS_EVENT_MESSAGE_RECEIVED_INBOX = "EVENT_MESSAGE_RECEIVED_INBOX"
const val BUS_EVENT_MESSAGES_LIST = "EVENT_MESSAGES_LIST"
const val BUS_EVENT_DELIVERED_MESSAGE = "EVENT_DELIVERED_MESSAGE"
const val BUS_EVENT_DELIVERED_ALL_MESSAGES = "EVENT_DELIVERED_ALL_MESSAGES"
const val BUS_EVENT_READ_MESSAGE = "EVENT_READ_MESSAGE"
const val BUS_EVENT_READ_ALL_MESSAGES = "EVENT_READ_ALL_MESSAGES"
const val BUS_EVENT_CHAT_COUNT = "BUS_EVENT_CHAT_COUNT"
const val BUS_EVENT_NOTIFICATION_COUNT = "BUS_EVENT_NOTIFICATION_COUNT"

const val BUS_EVENT_READ_ALL_MESSAGES_LOCALLY = "EVENT_READ_ALL_MESSAGES_LOCALLY"

const val BUS_EVENT_UPLOAD_STATUS = "EVENT_UPLOAD_STATUS"


/* endregion Events*/

