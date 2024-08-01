package com.caique.mynotes.repository
import com.caique.mynotes.dao.NoteDao
import com.caique.mynotes.model.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun getAllNotes(): List<NoteEntity> {
        return noteDao.searchAll()
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
