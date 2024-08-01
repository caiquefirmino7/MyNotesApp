package com.caique.mynotes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val timeStamp: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val imageUri: String? = null
) : Parcelable
