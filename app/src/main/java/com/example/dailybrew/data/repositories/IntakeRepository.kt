package com.example.dailybrew.data.repositories

import com.example.dailybrew.data.daos.IntakeDao
import com.example.dailybrew.data.entities.Intake
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class IntakeRepository(private val intakeDao: IntakeDao) {

    fun getAllIntakesForUser(userId: Long): Flow<List<Intake>> {
        return intakeDao.getAllIntakesForUser(userId)
    }

    fun getIntakesToday(userId: Long): Flow<List<Intake>> {
        val startOfDay = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return intakeDao.getIntakesForUserSince(userId, startOfDay)
    }

    fun getIntakesThisWeek(userId: Long): Flow<List<Intake>> {
        val startOfWeek = LocalDate.now().atStartOfDay().minusDays(6)
            .toEpochSecond(ZoneOffset.UTC)
        return intakeDao.getIntakesForUserSince(userId, startOfWeek)
    }

    fun getTotalCaffeineToday(userId: Long): Flow<Int?> {
        val startOfDay = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return intakeDao.getTotalCaffeineForUserSince(userId, startOfDay)
    }

    fun getDailyCaffeineTotal(userId: Long, date: LocalDate): Flow<Int?> {
        val startOfDay = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val endOfDay = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return intakeDao.getDailyCaffeineTotal(userId, startOfDay, endOfDay)
    }

    fun getDailyCaffeineByDrink(userId: Long, date: LocalDate): Flow<Map<Long, Int>> {
        val startOfDay = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val endOfDay = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return intakeDao.getDailyCaffeineByDrink(userId, startOfDay, endOfDay)
    }

    suspend fun insert(intake: Intake): Long {
        return intakeDao.insert(intake)
    }

    suspend fun update(intake: Intake) {
        intakeDao.update(intake)
    }

    suspend fun delete(intake: Intake) {
        intakeDao.delete(intake)
    }
}