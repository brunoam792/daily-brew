package com.example.dailybrew.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailybrew.data.repositories.DrinkRepository
import com.example.dailybrew.data.repositories.IntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class HistoryViewModel(
    private val intakeRepository: IntakeRepository,
    private val drinkRepository: DrinkRepository // Added drinkRepository
) : ViewModel() {

    // Current User ID
    private val currentUserId: Long = 1

    // Data for the weekly bar chart
    data class WeeklyData(
        val days: List<String> = emptyList(),
        val amounts: List<Int> = emptyList()
    )

    private val _weeklyData = MutableStateFlow(WeeklyData())
    val weeklyData: StateFlow<WeeklyData> = _weeklyData

    // Data for the daily doughnut chart
    data class DailyBreakdownItem(
        val drinkId: Long,
        val drinkName: String,
        val amount: Int,
        val percentage: Float
    )

    data class DailyBreakdown(
        val date: LocalDate = LocalDate.now(),
        val totalAmount: Int = 0,
        val items: List<DailyBreakdownItem> = emptyList()
    )

    private val _dailyBreakdown = MutableStateFlow(DailyBreakdown())
    val dailyBreakdown: StateFlow<DailyBreakdown> = _dailyBreakdown

    // Initialize with the current week's data
    init {
        loadWeeklyData()
        loadDailyBreakdown(LocalDate.now())
    }

    // Load weekly data - improved implementation
    private fun loadWeeklyData() {
        viewModelScope.launch {
            val days = mutableListOf<String>()
            val amounts = mutableListOf<Int>()
            val today = LocalDate.now()

            // Prepare day names for the last week
            for (i in 6 downTo 0) {
                val date = today.minusDays(i.toLong())
                // Get short day name in user's locale
                val dayName = date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT, Locale.getDefault()
                )
                days.add(dayName)
            }

            // Initialize amounts with zeros
            amounts.addAll(List(7) { 0 })

            // We'll now collect data for all days in one go
            intakeRepository.getIntakesThisWeek(currentUserId).collect { intakes ->
                // Reset amounts
                for (i in amounts.indices) {
                    amounts[i] = 0
                }

                // Sum up intakes by day
                intakes.forEach { intake ->
                    val intakeDate = LocalDate.ofEpochDay(intake.timestamp / 86400)
                    val daysAgo = today.toEpochDay() - intakeDate.toEpochDay()

                    if (daysAgo in 0..6) {
                        val index = 6 - daysAgo.toInt()
                        amounts[index] = amounts[index] + intake.totalCaffeine
                    }
                }

                _weeklyData.value = WeeklyData(days, amounts.toList())
            }
        }
    }

    // Load consumption details for a specific day
    fun loadDailyBreakdown(date: LocalDate) {
        viewModelScope.launch {
            // First, get all drinks to have names ready
            val drinksMap = mutableMapOf<Long, String>()
            drinkRepository.allDrinks.collect { drinks ->
                drinks.forEach { drink ->
                    drinksMap[drink.drinkId] = drink.name
                }

                // Once we have the drinks, get the daily breakdown
                intakeRepository.getDailyCaffeineByDrink(currentUserId, date).collect { dailyData ->
                    if (dailyData.isNotEmpty()) {
                        var totalAmount = 0

                        // Calculate total amount
                        dailyData.values.forEach { amount ->
                            totalAmount += amount
                        }

                        // Create breakdown items
                        val items = dailyData.map { (drinkId, amount) ->
                            DailyBreakdownItem(
                                drinkId = drinkId,
                                drinkName = drinksMap[drinkId] ?: "Unknown Drink",
                                amount = amount,
                                percentage = amount.toFloat() / totalAmount
                            )
                        }.sortedByDescending { it.amount }

                        _dailyBreakdown.value = DailyBreakdown(
                            date = date,
                            totalAmount = totalAmount,
                            items = items
                        )
                    } else {
                        // No data for this day
                        _dailyBreakdown.value = DailyBreakdown(
                            date = date,
                            totalAmount = 0,
                            items = emptyList()
                        )
                    }
                }
            }
        }
    }

    // Refresh data - can be called after adding new entries
    fun refreshData() {
        loadWeeklyData()
        loadDailyBreakdown(LocalDate.now())
    }

    class HistoryViewModelFactory(
        private val intakeRepository: IntakeRepository,
        private val drinkRepository: DrinkRepository // Added parameter
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(intakeRepository, drinkRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}