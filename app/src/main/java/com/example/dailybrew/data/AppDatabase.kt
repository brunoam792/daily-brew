package com.example.dailybrew.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dailybrew.data.daos.DailyLimitDao
import com.example.dailybrew.data.daos.DrinkDao
import com.example.dailybrew.data.daos.IntakeDao
import com.example.dailybrew.data.daos.UserDao
import com.example.dailybrew.data.entities.DailyLimit
import com.example.dailybrew.data.entities.Drink
import com.example.dailybrew.data.entities.Intake
import com.example.dailybrew.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, DailyLimit::class, Drink::class, Intake::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dailyLimitDao(): DailyLimitDao
    abstract fun drinkDao(): DrinkDao
    abstract fun intakeDao(): IntakeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_brew_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val drinkDao = database.drinkDao()
                        val defaultDrinks = listOf(
                            Drink(name = "Espresso", caffeinePerServing = 63, servingSize = 30),
                            Drink(name = "Drip", caffeinePerServing = 95, servingSize = 240),
                            Drink(name = "Latte", caffeinePerServing = 63, servingSize = 240),
                        )
                        drinkDao.insertAll(defaultDrinks)

                        val userDao = database.userDao()
                        val defaultUser = User(name = "User", email = "user@example.com")
                        val userId = userDao.insert(defaultUser)

                        val dailyLimitDao = database.dailyLimitDao()
                        val defaultLimit = DailyLimit(userId = userId, limitAmount = 400)
                        dailyLimitDao.insert(defaultLimit)
                    }
                }
            }
        }
    }
}