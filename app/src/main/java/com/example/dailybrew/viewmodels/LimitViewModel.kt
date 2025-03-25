package com.example.dailybrew.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailybrew.data.entities.DailyLimit
import com.example.dailybrew.data.repositories.DailyLimitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LimitViewModel(
    private val dailyLimitRepository: DailyLimitRepository
) : ViewModel() {

    // Current User ID
    private val currentUserId: Long = 1

    private val _currentLimit = MutableStateFlow<Int?>(null)
    val currentLimit: StateFlow<Int?> = _currentLimit

    private val _inputLimit = MutableStateFlow("")
    val inputLimit: StateFlow<String> = _inputLimit

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        loadCurrentLimit()
    }

    private fun loadCurrentLimit() {
        viewModelScope.launch {
            dailyLimitRepository.getLatestLimitForUser(currentUserId).collect { limit ->
                _currentLimit.value = limit?.limitAmount
                _inputLimit.value = limit?.limitAmount?.toString() ?: ""
            }
        }
    }

    fun updateInputLimit(value: String) {
        _inputLimit.value = value
    }

    fun saveLimit() {
        viewModelScope.launch {
            val limitValue = _inputLimit.value.toIntOrNull()
            if (limitValue != null && limitValue > 0) {
                val newLimit = DailyLimit(
                    userId = currentUserId,
                    limitAmount = limitValue
                )
                dailyLimitRepository.insert(newLimit)
                _saveSuccess.value = true

                // Reset save success state after a delay
                kotlinx.coroutines.delay(3000)
                _saveSuccess.value = false
            }
        }
    }

    class LimitViewModelFactory(
        private val dailyLimitRepository: DailyLimitRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LimitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LimitViewModel(dailyLimitRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}