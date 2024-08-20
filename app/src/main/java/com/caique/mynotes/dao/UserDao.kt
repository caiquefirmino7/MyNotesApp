package com.caique.mynotes.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.caique.mynotes.model.UserEntity

@Dao
interface UserDao {

    // Inserir um novo usuário no banco de dados
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM user WHERE user = :userId AND password = :password")
    suspend fun authenticateUser(userId: String, password: String): UserEntity?

}
