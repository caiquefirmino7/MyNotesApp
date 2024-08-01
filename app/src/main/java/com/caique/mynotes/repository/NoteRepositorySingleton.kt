package com.caique.mynotes.repository

import android.content.Context
import com.caique.mynotes.database.NotesDatabase

object NoteRepositorySingleton {
    private var repository: NoteRepository? = null

    fun getRepository(context: Context): NoteRepository {
        return repository ?: synchronized(this) {
            val database = NotesDatabase.getDatabase(context)
            val instance = NoteRepository(database.noteDao())
            repository = instance
            instance
        }
    }
}