package com.massttr.user.utils



enum class DocType(val type: Int) {
    DOC_DOCUMENT(1), // driving
    DOC_MEDICAL(2),  // health
    DOC_EDUCATION(3), //eduction
    DOC_OTHERS(4)    // other
}

enum class NewDocType(val type: Int){
    DOC_NATIONAL_ID_FRONT(1),
    DOC_NATIONAL_ID_BACK(2),
    DOC_RESIDENCE_CARD(3),
    DOC_RESUME(4),
}

enum class SuccessiveType(val type: Int) {
    REGISTRATION_EMAIL_VERIFIED(0),
    REGISTERED(1),
    FORGOT_PASSWORD_EMAIL_VERIFIED(2),
    START_JOB_CONFIRMED(3),
    START_TASK(4),
    END_TASK(5),
    VIEW_RECEIPT_TYPE(6),
    END_TASK_CONFIRMED(7)
}

enum class NotificationType(val type: String){
    NEW_JOB("NEWJOB"),
    JOB_STATUS("JOBSTATUS"),
    DOCUMENTS_STATUS("DOCUMENTSTATUS"),
    PRICE_CHANGE("PRICECHANGE")
}


