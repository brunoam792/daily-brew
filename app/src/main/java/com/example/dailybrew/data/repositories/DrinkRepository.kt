package com.example.dailybrew.data.repositories

import com.example.dailybrew.data.daos.DrinkDao
import com.example.dailybrew.data.entities.Drink
import kotlinx.coroutines.flow.Flow

class DrinkRepository(private val drinkDao: DrinkDao) {

    val allDrinks: Flow<List<Drink>> = drinkDao.getAllDrinks()

    fun getDrinkById(drinkId: Long): Flow<Drink?> {
        return drinkDao.getDrinkById(drinkId)
    }

    suspend fun insert(drink: Drink): Long {
        return drinkDao.insert(drink)
    }

    suspend fun insertAll(drinks: List<Drink>): List<Long> {
        return drinkDao.insertAll(drinks)
    }

    suspend fun update(drink: Drink) {
        drinkDao.update(drink)
    }

    suspend fun delete(drink: Drink) {
        drinkDao.delete(drink)
    }
}