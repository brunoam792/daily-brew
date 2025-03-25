package com.example.dailybrew

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailybrew.viewmodels.HomeViewModel

@Composable
fun HomeScreen() {
    // Get the application context
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication

    // Create the ViewModel with the factory
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(
            application.intakeRepository,
            application.dailyLimitRepository
        )
    )

    // Collect the ViewModel state
    val caffeineStatus by homeViewModel.caffeineStatus.collectAsState()

    // UI implementation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Screen title
            Text(
                text = "Caffeine Consumption",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Visual progress indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Progress indicator
                CircularProgressIndicator(
                    progress = { caffeineStatus.percentOfLimit.coerceIn(0f, 1f) },
                    modifier = Modifier.size(200.dp),
                    strokeWidth = 12.dp,
                    color = when {
                        caffeineStatus.percentOfLimit > 0.9f -> Color.Red
                        caffeineStatus.percentOfLimit > 0.75f -> Color(0xFFFFA000) // Orange
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                // Central information
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${caffeineStatus.currentAmount}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "mg",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card with limit information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daily Limit",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${caffeineStatus.dailyLimit} mg",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Status message
                    val statusText = when {
                        caffeineStatus.percentOfLimit >= 1.0f -> "Limit exceeded!"
                        caffeineStatus.percentOfLimit > 0.9f -> "Close to the limit!"
                        caffeineStatus.percentOfLimit > 0.75f -> "Moderate consumption"
                        caffeineStatus.percentOfLimit > 0.5f -> "Adequate consumption"
                        caffeineStatus.percentOfLimit > 0.25f -> "Low consumption"
                        else -> "Very low consumption"
                    }

                    val statusColor = when {
                        caffeineStatus.percentOfLimit >= 1.0f -> Color.Red
                        caffeineStatus.percentOfLimit > 0.9f -> Color(0xFFFFA000) // Orange
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}