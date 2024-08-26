package com.caique.mynotes.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caique.mynotes.dao.NoteDao
import com.caique.mynotes.dao.UserDao
import com.caique.mynotes.model.NoteEntity
import com.caique.mynotes.model.UserEntity


@Database(entities = [NoteEntity::class, UserEntity::class], version = 1, exportSchema = true)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "note_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}