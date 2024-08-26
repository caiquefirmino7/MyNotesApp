package com.caique.mynotes.repository

import com.caique.mynotes.dao.NoteDao
import com.caique.mynotes.model.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByUser(userId: String): Flow<List<NoteEntity>> {
        return noteDao.searchAllByUser(userId)
    }

    suspend fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return noteDao.searchById(id)
    }

}
