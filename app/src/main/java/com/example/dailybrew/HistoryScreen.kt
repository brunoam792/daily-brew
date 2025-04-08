package com.example.dailybrew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailybrew.ui.theme.CoffeeBrown
import com.example.dailybrew.ui.theme.CremeBg
import com.example.dailybrew.viewmodels.HistoryViewModel
import java.time.LocalDate

@Composable
fun HistoryScreen() {
    // Get the application context
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication

    // Create the ViewModel with the factory
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.HistoryViewModelFactory(
            application.intakeRepository,
            application.drinkRepository  // Pass drinkRepository
        )
    )

    // Collect the ViewModel state
    val weeklyData by historyViewModel.weeklyData.collectAsState()
    val dailyBreakdown by historyViewModel.dailyBreakdown.collectAsState()

    // Chart view state
    var isWeeklyView by remember { mutableStateOf(true) }

    // Effect to refresh data when screen is shown
    LaunchedEffect(Unit) {
        historyViewModel.refreshData()
    }

    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Spacer em vez do título "Daily Brew"
        Spacer(modifier = Modifier.height(40.dp))

        // Title card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = CremeBg
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isWeeklyView) "Your weekly intake" else "Your daily intake per type",
                color = CoffeeBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Chart card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = CremeBg
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isWeeklyView) {
                    // Weekly bar chart
                    WeeklyChartPlaceholder(weeklyData)
                } else {
                    // Daily breakdown donut chart
                    DailyPieChartPlaceholder(dailyBreakdown)
                }
            }
        }

        // Toggle button
        Button(
            onClick = { isWeeklyView = !isWeeklyView },
            colors = ButtonDefaults.buttonColors(
                containerColor = CoffeeBrown
            ),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .widthIn(min = 150.dp)
        ) {
            Text(text = if (isWeeklyView) "Daily" else "Weekly")
        }
    }
}

@Composable
fun WeeklyChartPlaceholder(weeklyData: HistoryViewModel.WeeklyData) {
    if (weeklyData.days.isEmpty() || weeklyData.amounts.isEmpty()) {
        Text(
            text = "No data available for the week",
            color = CoffeeBrown,
            textAlign = TextAlign.Center
        )
        return
    }

    // Simple placeholder for bar chart
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weekly Caffeine Intake",
            color = CoffeeBrown,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Horizontal bars representing daily intake
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val maxAmount = weeklyData.amounts.maxOrNull()?.toFloat() ?: 1f

            weeklyData.days.forEachIndexed { index, day ->
                if (index < weeklyData.amounts.size) {
                    val amount = weeklyData.amounts[index]
                    val height = if (maxAmount > 0) (amount / maxAmount) * 180f else 0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Show amount above the bar
                        Text(
                            text = "$amount",
                            color = CoffeeBrown,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // The bar itself
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(height.dp.coerceAtLeast(2.dp)) // Minimum height for visibility
                                .background(CoffeeBrown)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Day label
                        Text(
                            text = day,
                            color = CoffeeBrown,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyPieChartPlaceholder(dailyBreakdown: HistoryViewModel.DailyBreakdown) {
    if (dailyBreakdown.items.isEmpty()) {
        Text(
            text = "No data available for today",
            color = CoffeeBrown,
            textAlign = TextAlign.Center
        )
        return
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Date display
        Text(
            text = "Today's Breakdown",
            color = CoffeeBrown,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Pie chart placeholder
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            // Center text showing total
            Text(
                text = "${dailyBreakdown.totalAmount}",
                color = CoffeeBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "mg total",
                color = CoffeeBrown,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 28.dp)
            )

            // Simple circular progress indicator
            CircularProgressIndicator(
                progress = { 0.75f }, // Fixed value for design
                modifier = Modifier.size(180.dp),
                strokeWidth = 24.dp,
                color = CoffeeBrown,
                trackColor = CremeBg
            )
        }

        // Legend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            dailyBreakdown.items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(CoffeeBrown)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.drinkName,
                        color = CoffeeBrown,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${item.amount} mg (${(item.percentage * 100).toInt()}%)",
                        color = CoffeeBrown,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}