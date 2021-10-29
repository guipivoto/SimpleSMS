package com.pivoto.simplesms.message

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.Telephony.Sms
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class MessageRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    MessageRepository {

    override suspend fun getMessages(): List<Message> {
        val result: MutableList<Message> = mutableListOf()

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms"),
            arrayOf(BaseColumns._ID, Sms.ADDRESS, Sms.DATE, Sms.BODY),
            null,
            null,
            null
        )

        if (cursor?.moveToFirst() == true) {
            val idCol = cursor.getColumnIndex(BaseColumns._ID)
            val addressCol = cursor.getColumnIndex(Sms.ADDRESS)
            val dateCol = cursor.getColumnIndex(Sms.DATE)
            val bodyCol = cursor.getColumnIndex(Sms.BODY)

            do {
                result.add(Message(
                    cursor.getInt(idCol),
                    cursor.getString(addressCol),
                    cursor.getLong(dateCol),
                    cursor.getString(bodyCol)
                ))
            } while (cursor.moveToNext())
        }
        cursor?.close()

        return result
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleProvider {
    @Singleton
    @Binds
    abstract fun bindRepository(impl: MessageRepositoryImpl): MessageRepository
}