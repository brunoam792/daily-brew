package com.example.dailybrew.data.daos

import androidx.room.*
import com.example.dailybrew.data.entities.Intake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Class to represent the result of the caffeine query by drink
data class DrinkCaffeineAmount(
    @ColumnInfo(name = "drinkId") val drinkId: Long,
    @ColumnInfo(name = "totalCaffeine") val totalCaffeine: Int
)

@Dao
interface IntakeDao {
    @Query("SELECT * FROM intakes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllIntakesForUser(userId: Long): Flow<List<Intake>>

    @Query("SELECT * FROM intakes WHERE userId = :userId AND timestamp >= :startTime ORDER BY timestamp DESC")
    fun getIntakesForUserSince(userId: Long, startTime: Long): Flow<List<Intake>>

    @Query("SELECT SUM(totalCaffeine) FROM intakes WHERE userId = :userId AND timestamp >= :startTime")
    fun getTotalCaffeineForUserSince(userId: Long, startTime: Long): Flow<Int?>

    @Insert
    suspend fun insert(intake: Intake): Long

    @Update
    suspend fun update(intake: Intake)

    @Delete
    suspend fun delete(intake: Intake)

    @Query("SELECT SUM(totalCaffeine) FROM intakes WHERE userId = :userId AND timestamp >= :startOfDay AND timestamp < :endOfDay")
    fun getDailyCaffeineTotal(userId: Long, startOfDay: Long, endOfDay: Long): Flow<Int?>

    // Changing the query to return a list of DrinkCaffeineAmount instead of a Map directly
    @Query("SELECT drinkId, SUM(totalCaffeine) as totalCaffeine FROM intakes WHERE userId = :userId AND timestamp >= :startOfDay AND timestamp < :endOfDay GROUP BY drinkId")
    fun getDailyCaffeineByDrinkRaw(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<DrinkCaffeineAmount>>

    // Extension method to transform the list into a Map
    fun getDailyCaffeineByDrink(userId: Long, startOfDay: Long, endOfDay: Long): Flow<Map<Long, Int>> {
        return getDailyCaffeineByDrinkRaw(userId, startOfDay, endOfDay).map { result ->
            result.associate { it.drinkId to it.totalCaffeine }
        }
    }
}