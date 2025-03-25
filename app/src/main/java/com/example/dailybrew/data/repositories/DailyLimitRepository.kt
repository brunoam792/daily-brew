package com.example.dailybrew.data.repositories

import com.example.dailybrew.data.daos.DailyLimitDao
import com.example.dailybrew.data.entities.DailyLimit
import kotlinx.coroutines.flow.Flow

class DailyLimitRepository(private val dailyLimitDao: DailyLimitDao) {

    fun getLatestLimitForUser(userId: Long): Flow<DailyLimit?> {
        return dailyLimitDao.getLatestLimitForUser(userId)
    }

    suspend fun insert(limit: DailyLimit): Long {
        return dailyLimitDao.insert(limit)
    }

    suspend fun update(limit: DailyLimit) {
        dailyLimitDao.update(limit)
    }
}