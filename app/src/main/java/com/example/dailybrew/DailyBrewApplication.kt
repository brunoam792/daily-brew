package com.example.dailybrew

import android.app.Application
import com.example.dailybrew.data.AppDatabase
import com.example.dailybrew.data.repositories.DailyLimitRepository
import com.example.dailybrew.data.repositories.DrinkRepository
import com.example.dailybrew.data.repositories.IntakeRepository
import com.example.dailybrew.data.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DailyBrewApplication : Application() {
    // Coroutine scope for the application
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy database initialization
    private val database by lazy {
        AppDatabase.getDatabase(this, applicationScope)
    }

    // Repositories
    val userRepository by lazy { UserRepository(database.userDao()) }
    val dailyLimitRepository by lazy { DailyLimitRepository(database.dailyLimitDao()) }
    val drinkRepository by lazy { DrinkRepository(database.drinkDao()) }
    val intakeRepository by lazy { IntakeRepository(database.intakeDao()) }
}