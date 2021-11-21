package com.pivoto.simplesms.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
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
    lateinit var repository: MessageRepository

    @Inject
    lateinit var notification: MessageNotification

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

    private fun handleSmsReceived(extras: Bundle?) {
        if (extras == null) {
            return
        }
        val pduArray = extras.get("pdus")
        val format = extras.getString("format")

        Log.v(Tags.RECEIVER, "New SMS. pdus $pduArray format: $format")

        if (pduArray != null && format != null && pduArray is Array<*>) {
            pduArray.forEach { pdu ->
                if (pdu is ByteArray) {
                    SmsMessage.createFromPdu(pdu, format)?.also {
                        val message = Message(it)

                        runBlocking {
                            launch {
                                repository.insertNewMessage(message)
                            }
                        }

                        Log.v(Tags.RECEIVER, "New SMS: $message")
                        notification.displayNotification(message)
                    }
                }
            }
        }
    }

    private fun handleDeleteSms(extras: Bundle?) {
        if (extras == null) {
            return
        }

        notification.clearNotification(extras)

        val address: String = extras.getString("address", "")
        val date: Long = extras.getLong("date", -1)

        Log.v(Tags.RECEIVER, "onReceive() delete message from: $address date: $date")

        runBlocking {
            launch {
                repository.deleteMessage(address, date)
            }
        }
    }
}