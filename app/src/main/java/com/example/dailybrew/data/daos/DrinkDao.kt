package com.example.dailybrew.data.daos

import androidx.room.*
import com.example.dailybrew.data.entities.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Query("SELECT * FROM drinks ORDER BY name ASC")
    fun getAllDrinks(): Flow<List<Drink>>

    @Query("SELECT * FROM drinks WHERE drinkId = :drinkId")
    fun getDrinkById(drinkId: Long): Flow<Drink?>

    @Insert
    suspend fun insert(drink: Drink): Long

    @Insert
    suspend fun insertAll(drinks: List<Drink>): List<Long>

    @Update
    suspend fun update(drink: Drink)

    @Delete
    suspend fun delete(drink: Drink)
}