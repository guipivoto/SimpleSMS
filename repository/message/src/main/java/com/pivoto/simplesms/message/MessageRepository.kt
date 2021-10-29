package com.pivoto.simplesms.message

sealed interface MessageRepository {

    fun getMessages(): String
}