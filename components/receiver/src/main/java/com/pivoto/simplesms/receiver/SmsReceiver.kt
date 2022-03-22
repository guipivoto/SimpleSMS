package com.pivoto.simplesms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import com.pivoto.simplesms.contact.ContactRepository
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.message.MessageRepository
import com.pivoto.simplesms.notification.MessageNotification
import com.pivoto.simplesms.receiver.util.Tags
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var notification: MessageNotification

    @Inject
    lateinit var contactRepository: ContactRepository

    override fun onReceive(context: Context, intent: Intent) {

        intent.action.also { action ->
            Log.v(Tags.RECEIVER, "onReceiver: $action")
            when (action) {
                "android.provider.Telephony.SMS_DELIVER", Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                    handleSmsReceived(intent.extras)
                }
                "com.pivoto.simplesms.DELETE_MESSAGE" -> {
                    handleDeleteSms(intent.extras)
                }
                "com.pivoto.simplesms.BLOCK_CONTACT_MESSAGE" -> {
                    // Block contact
                    blockContact(intent.extras)
                }
                "com.pivoto.simplesms.TEST_INTENT" -> {
                    // Test Intent
                }
                else -> {
                    Log.w(Tags.RECEIVER, "onReceiver unexpected action: $action")
                }
            }
        }
    }

    private fun blockContact(extras: Bundle?) {
        if (extras == null) {
            return
        }
        extras.getString(ADDRESS_KEY)?.also {
            Log.v(Tags.RECEIVER, "Contact to block: $it")
            runBlocking {
                contactRepository.blockNumber(it)
            }
        }
    }

    private fun handleSmsReceived(extras: Bundle?) {
        if (extras == null) {
            return
        }
        val pduArray = extras.get(PDU_KEY)
        val format = extras.getString(FORMAT_KEY)

        Log.v(Tags.RECEIVER, "New SMS. pdus $pduArray format: $format")

        if (pduArray != null && format != null && pduArray is Array<*>) {
            pduArray.forEach { pdu ->
                if (pdu is ByteArray) {
                    SmsMessage.createFromPdu(pdu, format)?.also {
                        val message = Message(it)

                        runBlocking {
                            launch {
                                messageRepository.insertNewMessage(message)

                                Log.v(Tags.RECEIVER, "New SMS: $message")

                                if(!contactRepository.isBlocked(message.address)) {
                                    notification.displayNotification(message)
                                } else {
                                    Log.v(Tags.RECEIVER, "Don't notify. Number blocked")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleDeleteSms(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val address: String = extras.getString(ADDRESS_KEY, "")
        val date: Long = extras.getLong(DATE_KEY, -1)

        Log.v(Tags.RECEIVER, "onReceive() delete message from: $address date: $date")

        runBlocking {
            launch {
                messageRepository.deleteMessage(address, date)
                notification.clearNotification(extras)
            }
        }
    }

    companion object {
        private const val ADDRESS_KEY = "address"
        private const val DATE_KEY = "date"
        private const val PDU_KEY = "pdus"
        private const val FORMAT_KEY = "pdus"
    }
}