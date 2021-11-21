package com.pivoto.simplesms.message

import android.telephony.SmsMessage

data class Message(val id: Int = 0, val address: String, val date: Long) {

    constructor(sms: SmsMessage): this(0, sms.displayOriginatingAddress, sms.timestampMillis) {
        dateSent = sms.timestampMillis
        protocol = sms.protocolIdentifier
        subject = sms.pseudoSubject
        body = sms.displayMessageBody
        replyPath = if (sms.isReplyPathPresent) 1 else 0
        serviceCenter = sms.serviceCenterAddress
    }

    var body: String? = null
    var serviceCenter: String? = null
    var replyPath: Int = 0
    var subject: String? = null
    var seen: Int = 0
    var read: Int = 0
    var protocol: Int = 0
    var dateSent: Long = 0L
}
