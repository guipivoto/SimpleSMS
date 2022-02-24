package com.pivoto.simplesms.contact

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val address: String,
    val isBlocked: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}