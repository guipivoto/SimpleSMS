package com.pivoto.simplesms.message

sealed interface MessageRepository {

    suspend fun getMessages(): List<Message>

    suspend fun insertNewMessage(message: Message)
}