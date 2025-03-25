package com.example.dailybrew.data.daos

import androidx.room.*
import com.example.dailybrew.data.entities.DailyLimit
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLimitDao {
    @Query("SELECT * FROM daily_limits WHERE userId = :userId ORDER BY dateCreated DESC LIMIT 1")
    fun getLatestLimitForUser(userId: Long): Flow<DailyLimit?>

    @Insert
    suspend fun insert(limit: DailyLimit): Long

    @Update
    suspend fun update(limit: DailyLimit)
}