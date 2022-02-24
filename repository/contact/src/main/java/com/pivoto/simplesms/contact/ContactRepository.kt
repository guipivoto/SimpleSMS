package com.pivoto.simplesms.contact

sealed interface ContactRepository {

    suspend fun blockNumber(address: String)

    suspend fun isBlocked(address: String): Boolean
}