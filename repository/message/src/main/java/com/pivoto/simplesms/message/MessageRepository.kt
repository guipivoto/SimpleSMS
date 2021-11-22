package com.pivoto.simplesms.message

sealed interface MessageRepository {

    suspend fun getConversations(): List<Message>

    suspend fun getConversation(address: String): List<Message>

    suspend fun insertNewMessage(message: Message)

    suspend fun deleteMessage(address: String, date: Long)
}