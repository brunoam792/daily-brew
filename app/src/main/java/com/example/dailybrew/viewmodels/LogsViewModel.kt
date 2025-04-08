package com.example.dailybrew.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailybrew.data.entities.Drink
import com.example.dailybrew.data.entities.Intake
import com.example.dailybrew.data.repositories.DrinkRepository
import com.example.dailybrew.data.repositories.IntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LogsViewModel(
    private val intakeRepository: IntakeRepository,
    private val drinkRepository: DrinkRepository,
    private val historyViewModel: HistoryViewModel? = null // Optional reference to HistoryViewModel
) : ViewModel() {

    // Current user ID (in a real app, this would come from authentication)
    private val currentUserId: Long = 1

    // UI model for a log entry
    data class LogEntryUiModel(
        val id: Long,
        val drinkName: String,
        val amount: Int, // in mg
        val formattedTime: String
    )

    private val _logEntries = MutableStateFlow<List<LogEntryUiModel>>(emptyList())
    val logEntries: StateFlow<List<LogEntryUiModel>> = _logEntries

    // Available drinks for selection
    private val _availableDrinks = MutableStateFlow<List<Drink>>(emptyList())
    val availableDrinks: StateFlow<List<Drink>> = _availableDrinks

    // State for ongoing operations
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for operation messages
    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Load available drinks
            drinkRepository.allDrinks.collect {
                _availableDrinks.value = it
                _isLoading.value = false
            }
        }

        // Combine intakes with drink information
        viewModelScope.launch {
            combine(
                intakeRepository.getAllIntakesForUser(currentUserId),
                drinkRepository.allDrinks
            ) { intakes, drinks ->
                val drinksMap = drinks.associateBy { it.drinkId }

                intakes.map { intake ->
                    val drink = drinksMap[intake.drinkId]
                    val timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(intake.timestamp),
                        ZoneId.systemDefault()
                    )
                    val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")

                    LogEntryUiModel(
                        id = intake.intakeId,
                        drinkName = drink?.name ?: "Unknown Drink",
                        amount = intake.totalCaffeine,
                        formattedTime = timestamp.format(formatter)
                    )
                }.sortedByDescending { it.id } // Sort by ID instead of formatted time for better chronological order
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            ).collect {
                _logEntries.value = it
            }
        }
    }

    // Add a new intake log
    fun addIntake(drinkId: Long, servings: Float) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                drinkRepository.getDrinkById(drinkId).collect { drink ->
                    drink?.let {
                        val totalCaffeine = (it.caffeinePerServing * servings).toInt()
                        val intake = Intake(
                            userId = currentUserId,
                            drinkId = drinkId,
                            servings = servings,
                            totalCaffeine = totalCaffeine
                        )
                        intakeRepository.insert(intake)
                        _operationMessage.value = "Caffeine intake recorded successfully!"

                        // Refresh the history view if available
                        historyViewModel?.refreshData()
                    }
                }
            } catch (e: Exception) {
                _operationMessage.value = "Error recording intake: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete an intake log
    fun deleteIntake(intakeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Find the intake in the logs
                intakeRepository.getAllIntakesForUser(currentUserId).collect { intakes ->
                    intakes.find { it.intakeId == intakeId }?.let { intake ->
                        intakeRepository.delete(intake)
                        _operationMessage.value = "Record deleted successfully!"

                        // Refresh the history view if available
                        historyViewModel?.refreshData()
                    }
                }
            } catch (e: Exception) {
                _operationMessage.value = "Error deleting record: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear operation message after reading
    fun clearOperationMessage() {
        _operationMessage.value = null
    }

    class LogsViewModelFactory(
        private val intakeRepository: IntakeRepository,
        private val drinkRepository: DrinkRepository,
        private val historyViewModel: HistoryViewModel? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LogsViewModel(intakeRepository, drinkRepository, historyViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}