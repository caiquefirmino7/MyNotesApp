package com.caique.mynotes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.caique.mynotes.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Inserir um novo usuário no banco de dados
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM user WHERE user = :userId AND password = :password")
    suspend fun authenticateUser(userId: String, password: String): UserEntity?


    @Query("SELECT * FROM user WHERE user = :userId")
    fun getUserById(userId: String): Flow<UserEntity>

    @Query("UPDATE user SET name = :newName  WHERE user = :userId")
        suspend fun updateUserName(userId: String, newName: String)

    @Query("UPDATE user SET password = :newPassword WHERE user = :userId")
        suspend fun updateUserPassword(userId: String, newPassword: String)

        @Delete
        suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM notes WHERE  userId= :userId")
    suspend fun deleteNotesByUserId(userId: String)
}
