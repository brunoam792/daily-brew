package com.example.dailybrew.data.repositories

import com.example.dailybrew.data.daos.UserDao
import com.example.dailybrew.data.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }
}