package com.caique.mynotes.repository

import com.caique.mynotes.dao.UserDao
import com.caique.mynotes.model.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun authenticateUser(userId: String, password: String): UserEntity? {
        return userDao.authenticateUser(userId, password)
    }


}
