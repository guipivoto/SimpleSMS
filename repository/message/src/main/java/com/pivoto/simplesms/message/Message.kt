package com.pivoto.simplesms.message

import android.telephony.SmsMessage

data class Message(val id: Int = 0, val address: String, val date: Long, val body: String) {

    constructor(sms: SmsMessage): this(0, sms.displayOriginatingAddress, sms.timestampMillis, sms.displayMessageBody) {
        dateSent = sms.timestampMillis
        protocol = sms.protocolIdentifier
        subject = sms.pseudoSubject
        replyPath = if (sms.isReplyPathPresent) 1 else 0
        serviceCenter = sms.serviceCenterAddress
    }

    var serviceCenter: String? = null
    var replyPath: Int = 0
    var subject: String? = null
    var seen: Int = 0
    var read: Int = 0
    var protocol: Int = 0
    var dateSent: Long = 0L
}
