package com.pivoto.simplesms.message

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.Telephony.Sms
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class MessageRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    MessageRepository {

    override suspend fun getMessages(): List<Message> = withContext(Dispatchers.IO) {
        val result: MutableList<Message> = mutableListOf()

        context.contentResolver.query(
            Uri.parse("content://sms"),
            arrayOf("DISTINCT ${Sms.ADDRESS}", BaseColumns._ID, Sms.DATE, Sms.BODY),
            "${Sms.ADDRESS} IS NOT NULL) GROUP BY (${Sms.ADDRESS}",
            null, null
        ).use { cursor ->

            while (cursor?.moveToNext() == true) {
                val idCol = cursor.getColumnIndex(BaseColumns._ID)
                val addressCol = cursor.getColumnIndex(Sms.ADDRESS)
                val dateCol = cursor.getColumnIndex(Sms.DATE)
                val bodyCol = cursor.getColumnIndex(Sms.BODY)
                result.add(
                    Message(
                        cursor.getInt(idCol),
                        cursor.getString(addressCol),
                        cursor.getLong(dateCol),
                        cursor.getString(bodyCol)
                    )
                )
            }
        }
        result
    }

    override suspend fun insertNewMessage(message: Message): Unit = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(Sms.Inbox.ADDRESS, message.address)
            put(Sms.DATE, message.date)
            put(Sms.Inbox.BODY, message.body)
            put(Sms.Inbox.DATE_SENT, message.dateSent)
            put(Sms.Inbox.PROTOCOL, message.protocol)
            put(Sms.Inbox.READ, message.read)
            put(Sms.Inbox.SEEN, message.seen)
            put(Sms.Inbox.SUBJECT, message.subject)
            put(Sms.Inbox.REPLY_PATH_PRESENT, message.replyPath)
            put(Sms.Inbox.SERVICE_CENTER, message.serviceCenter)
        }
        context.contentResolver.insert(Sms.Inbox.CONTENT_URI, values)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleProvider {
    @Singleton
    @Binds
    abstract fun bindRepository(impl: MessageRepositoryImpl): MessageRepository
}