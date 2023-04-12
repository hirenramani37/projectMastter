package com.massttr.user.utils

/**
 * Created by Hiren on 22-9-2021.
 */


//const val DOMAIN = ""   //  live chat
const val DOMAIN = ""  //  testing chat

//const val API_BASE_URL = ""
const val SOCKET_HOST = "http://$DOMAIN:3000"

//const val SERVER_DOMAIN = ""  // Live Server
const val SERVER_DOMAIN = ""  // Testing Server

const val FORMAT_MESSAGE_TIME = "h:mm a"


const val PRM_DEVICE_TOKEN = "device_token"
const val ANDROID_TYPE = "A"
const val PRM_DOCUMENT = "document"
const val PRM_NAME = "name"
const val PRM_TYPE = "type"
const val PRM_SELECTED_CATEGORY = "selectedcategory"
const val PRM_SELECTED_SUBCATEGORY = "selectedsubcategory"
const val PRM_DEFAULT_LANG = "default_lang"
const val PRM_FULL_NAME = "full_name"
const val PRM_COUNTRY_CODE = "country_code"
const val PRM_PHONE_NUMBER = "phone_no"
const val PRM_PASSWORD = "password"
const val PRM_BIRTH_DAY = "birth_date"
const val PRM_GENDER = "gender"
const val PRM_ZIP_CODE = "zipcode"
const val PRM_ADDRESS = "address"
const val PRM_STATE = "state"
const val PRM_CITY = "city"
const val PRM_ENV = "env"
const val PRM_COMPANY_NAME = "company_name"
const val PRM_COMPANY_VAT_NUMBER = "company_vat_no"
const val PRM_COMPANY_ZIP_CODE = "company_zipcode"
const val PRM_COMPANY_EMAIL = "company_email"
const val PRM_SEND_INVOICE = "is_send_invoice"
const val PRM_COMPANY_ADDRESS = "company_address"
const val PRM_CATEGORY_IDS = "category_ids"
const val PRM_CATEGORY_ID = "category_id"
const val PRM_COMMENTS = "comments"
const val PRM_SKILLS_IDS = "skill_ids"
const val PRM_SUBCATEGORY_IDS = "subcategory_ids"
const val PRM_PROFILE_PICTURE = "profile_picture"
const val EXTRA_RECEIPT_DETAIL = "EXTRA_RECEIPT_DETAIL"
const val EXTRA_JOB_STARTED = "EXTRA_JOB_STARTED"
const val EXTRA_JOB_ID = "EXTRA_JOB_ID"
const val EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION"
const val START_JOB_ACTIVITY = "START_JOB_ACTIVITY"
const val AVAILABLE_FIXDETAIL = "AVAILABLE_FIXDETAIL"
const val AVAILABLE_FIX_DETAIL = "AVAILABLE_FIX_DETAIL"
const val JOB_ACCEPT_ACTIVITY = "JOB_ACCEPT_ACTIVITY"
const val JOB_CANCEL_ACTIVITY = "JOB_CANCEL_ACTIVITY"
const val RESET_PASSWORD_ACTIVITY = "RESET_PASSWORD_ACTIVITY"
const val CHAT_ACTIVITY = "CHAT_ACTIVITY"
const val JOB_END_ACTIVITY = "JOB_END_ACTIVITY"
const val PRM_IMAGE = "image"
const val PRM_JOB_ID = "job_id"
const val PRM_STATUS = "status"
const val PRM_JOB_IMAGE_ID = "job_image_id"
const val PRM_START_JOB_TYPE = "1"
const val PRM_END_JOB_TYPE = "2"
const val PRM_JOB_ACCEPT_STATUS = "2"
const val PRM_JOB_STARTED = "3"
const val PRM_JOB_CANCELED = "6"
const val PRM_JOB_COMPLETED = "4"
const val PRM_FIX_STATUS = "is_online"
const val PRM_INTERSTED_JOB_ID = "job_id"


const val NOTIFICATION_FIXDETAIL = "NOTIFICATION_FIXDETAIL"
const val GOOGLETAG_FIXDETAIL = "GOOGLETAG_FIXDETAIL"

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

const val BUS_EVENT_CONNECTED = "EVENT_CONNECTED"
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
/* endregion Events*/
