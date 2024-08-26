package com.caique.mynotes.repository

import com.caique.mynotes.dao.UserDao
import com.caique.mynotes.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun authenticateUser(userId: String, password: String): UserEntity? {
        return userDao.authenticateUser(userId, password)
    }

    fun getUserById(userId: String): Flow<UserEntity> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUserName(userId: String, newName: String) {
        userDao.updateUserName(userId, newName)
    }


    suspend fun updateUserPassword(userId: String, newPassword: String) {
        userDao.updateUserPassword(userId, newPassword)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
        userDao.deleteNotesByUserId(user.user)
    }

}
