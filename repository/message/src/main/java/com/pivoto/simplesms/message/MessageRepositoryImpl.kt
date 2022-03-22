package com.pivoto.simplesms.message

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.Telephony.Sms
import android.util.Log
import com.pivoto.simplesms.message.util.Tags
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

    override suspend fun getConversations(): List<Message> = withContext(Dispatchers.IO) {
        val result: MutableList<Message> = mutableListOf()

        context.contentResolver.query(
            Uri.parse("content://sms"),
            null,
            "${Sms.ADDRESS} IS NOT NULL) GROUP BY (${Sms.ADDRESS}",
            null, null
        ).use { cursor ->

            while (cursor?.moveToNext() == true) {
                result.add(
                    Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Sms.ADDRESS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(Sms.DATE))
                    ).apply {
                        body = cursor.getString(cursor.getColumnIndexOrThrow(Sms.BODY))
                    }
                )
            }
        }
        result
    }

    override suspend fun getConversation(address: String): List<Message> {
        val result: MutableList<Message> = mutableListOf()
        context.contentResolver.query(
            Uri.parse("content://sms"),
            null,
            "${Sms.ADDRESS}=?",
            arrayOf(address), null
        ).use { cursor ->

            while (cursor?.moveToNext() == true) {
                result.add(
                    Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Sms.ADDRESS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(Sms.DATE))
                    ).apply {
                        body = cursor.getString(cursor.getColumnIndexOrThrow(Sms.BODY))
                    }
                )
            }
        }
        return result
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

    override suspend fun deleteMessage(address: String, date: Long) {
        context.contentResolver.query(
            Sms.CONTENT_URI,
            arrayOf(Sms._ID, Sms.ADDRESS, Sms.DATE),
            Sms.DATE + " = ? AND " + Sms.ADDRESS + " = ? ",
            arrayOf(date.toString(), address),
            null
        ).use { cursor ->
            if (compareValues(cursor?.count, 0) > 0 && cursor?.moveToFirst() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(Sms._ID))
                val foundAddress = cursor.getString(cursor.getColumnIndexOrThrow(Sms.ADDRESS))
                val foundDate = cursor.getLong(cursor.getColumnIndexOrThrow(Sms.DATE))
                Log.d(
                    Tags.REPOSITORY,
                    "deleteSMS() Result: " + cursor.count + " Message Deleted -  id: " + id + " address: " + foundAddress + " date: " + foundDate
                )
                val result =
                    context.contentResolver.delete(Uri.parse("content://sms/$id"), null, null)
                Log.d(Tags.REPOSITORY, "Messages deleted: $result")
            } else {
                Log.w(Tags.REPOSITORY, "cursor is null or empty: ${cursor?.count}")
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleProvider {
    @Singleton
    @Binds
    abstract fun bindRepository(impl: MessageRepositoryImpl): MessageRepository
}