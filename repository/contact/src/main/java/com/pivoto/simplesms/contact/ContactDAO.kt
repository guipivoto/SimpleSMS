package com.pivoto.simplesms.contact

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ContactDAO {

    @Insert(onConflict = REPLACE)
    fun addContact(contact: Contact)

    @Query("SELECT * FROM Contact WHERE address LIKE :address")
    fun getContact(address: String): Contact?
}
