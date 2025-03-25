package com.example.dailybrew.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dailybrew.data.repositories.DailyLimitRepository
import com.example.dailybrew.data.repositories.IntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val intakeRepository: IntakeRepository,
    private val dailyLimitRepository: DailyLimitRepository
) : ViewModel() {

    // Current User ID
    private val currentUserId: Long = 1

    // Caffeine status
    data class CaffeineStatus(
        val currentAmount: Int = 0,
        val dailyLimit: Int = 400,
        val percentOfLimit: Float = 0f
    )

    private val _caffeineStatus = MutableStateFlow(CaffeineStatus())
    val caffeineStatus: StateFlow<CaffeineStatus> = _caffeineStatus

    // Initialize
    init {
        viewModelScope.launch {
            combine(
                intakeRepository.getTotalCaffeineToday(currentUserId),
                dailyLimitRepository.getLatestLimitForUser(currentUserId)
            ) { totalCaffeine, dailyLimit ->
                val current = totalCaffeine ?: 0
                val limit = dailyLimit?.limitAmount ?: 400
                val percent = if (limit > 0) current.toFloat() / limit else 0f
                CaffeineStatus(current, limit, percent)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CaffeineStatus()
            ).collect {
                _caffeineStatus.value = it
            }
        }
    }

    class HomeViewModelFactory(
        private val intakeRepository: IntakeRepository,
        private val dailyLimitRepository: DailyLimitRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(intakeRepository, dailyLimitRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}