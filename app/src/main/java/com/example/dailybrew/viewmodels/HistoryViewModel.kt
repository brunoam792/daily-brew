package com.example.dailybrew.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailybrew.data.repositories.IntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryViewModel(
    private val intakeRepository: IntakeRepository
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

    // Initialize with the current week’s data
    init {
        loadWeeklyData()
        loadDailyBreakdown(LocalDate.now())
    }

    // Load weekly data
    private fun loadWeeklyData() {
        viewModelScope.launch {
            val days = mutableListOf<String>()
            val amounts = mutableListOf<Int>()

            for (i in 6 downTo 0) {
                val date = LocalDate.now().minusDays(i.toLong())
                val dayName = date.dayOfWeek.toString().take(3)
                days.add(dayName)

                intakeRepository.getDailyCaffeineTotal(currentUserId, date).collect { total ->
                    amounts.add(total ?: 0)

                    // If we have all the days, update the state
                    if (amounts.size == 7) {
                        _weeklyData.value = WeeklyData(days, amounts)
                    }
                }
            }
        }
    }

    // Load consumption details for a specific day
    fun loadDailyBreakdown(date: LocalDate) {
        viewModelScope.launch {
            // This is a placeholder – in a real implementation, you would need
            // to get drink names from the DrinkRepository based on the IDs
            val mockBreakdown = listOf(
                DailyBreakdownItem(1, "Espresso", 100, 0.4f),
                DailyBreakdownItem(2, "Latte", 150, 0.6f)
            )
            _dailyBreakdown.value = DailyBreakdown(
                date = date,
                totalAmount = 250,
                items = mockBreakdown
            )
        }
    }

    class HistoryViewModelFactory(
        private val intakeRepository: IntakeRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HistoryViewModel(intakeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
