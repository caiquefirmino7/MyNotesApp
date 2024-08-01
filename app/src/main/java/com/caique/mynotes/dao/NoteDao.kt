package com.caique.mynotes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.caique.mynotes.model.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    suspend fun searchAll(): List<NoteEntity>

    @Insert
    suspend fun insert(vararg note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Update
    suspend fun update(vararg update: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun searchById(id: Long): NoteEntity?
}
