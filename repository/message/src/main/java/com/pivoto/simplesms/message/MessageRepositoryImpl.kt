package com.pivoto.simplesms.message

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

class MessageRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    MessageRepository {
    override suspend fun getMessages(): List<Message> {
        delay(2000L)
        return listOf()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleProvider {
    @Singleton
    @Binds
    abstract fun bindRepository(impl: MessageRepositoryImpl): MessageRepository
}