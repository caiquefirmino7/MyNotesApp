package com.caique.mynotes.repository

import android.content.Context
import com.caique.mynotes.database.NotesDatabase


object UserRepositorySingleton {
    private var repository: UserRepository? = null

    fun getRepository(context: Context): UserRepository {
        return repository ?: synchronized(this) {
            val database = NotesDatabase.getDatabase(context)
            val instance = UserRepository(database.userDao())
            repository = instance
            instance
        }
    }
}
