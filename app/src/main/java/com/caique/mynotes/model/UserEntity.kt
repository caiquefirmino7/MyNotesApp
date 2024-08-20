package com.caique.mynotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey
    val user: String,
    val name: String,
    val password: String
)

