package com.pivoto.simplesms.contact

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class ContactRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    ContactRepository {

    private val contactDatabase by lazy {
        Room.databaseBuilder(context, ContactDatabase::class.java, "database-name").build()
    }

    override suspend fun blockNumber(address: String) = withContext(Dispatchers.IO) {
        contactDatabase.contactDao().addContact(Contact(address, true))
    }

    override suspend fun isBlocked(address: String) : Boolean = withContext(Dispatchers.IO) {
        contactDatabase.contactDao().getContact(address)?.isBlocked == true
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleProvider {
    @Singleton
    @Binds
    abstract fun bindRepository(impl: ContactRepositoryImpl): ContactRepository
}