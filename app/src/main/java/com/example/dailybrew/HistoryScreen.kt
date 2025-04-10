package com.example.dailybrew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import com.example.dailybrew.viewmodels.LimitViewModel

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

    // Simple placeholder for bar chart with improved layout
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weekly Caffeine Intake",
            color = CoffeeBrown,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Horizontal bars with fixed height container to prevent overflow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            // Calculate max height available for bars
            val maxAmount = weeklyData.amounts.maxOrNull()?.toFloat() ?: 1f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f), // Leave space for day labels
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.days.forEachIndexed { index, day ->
                    if (index < weeklyData.amounts.size) {
                        val amount = weeklyData.amounts[index]
                        // Calculate relative height - prevent it from being too tall
                        val relativeHeight = if (maxAmount > 0) (amount / maxAmount) else 0f

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Amount text above bar
                            Text(
                                text = "$amount",
                                color = CoffeeBrown,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            // The bar with responsive height
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(relativeHeight.coerceAtLeast(0.02f))
                                    .background(CoffeeBrown)
                            )
                        }
                    }
                }
            }

            // Day labels at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyData.days.forEach { day ->
                    Text(
                        text = day,
                        color = CoffeeBrown,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
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

    // Get the current daily limit
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication
    val limitViewModel: LimitViewModel = viewModel(
        factory = LimitViewModel.LimitViewModelFactory(
            application.dailyLimitRepository
        )
    )

    // Collect the current limit
    val currentLimit by limitViewModel.currentLimit.collectAsState()
    val dailyLimit = currentLimit ?: 400

    // Recalculate percentage based on the current limit
    val percentOfLimit = dailyBreakdown.totalAmount.toFloat() / dailyLimit

    // Generate colors for the chart
    val colors = remember(dailyBreakdown.items.size) {
        val baseColor = CoffeeBrown
        List(dailyBreakdown.items.size) { index ->
            when (index) {
                0 -> baseColor
                1 -> Color(0xFF9E6A4B) // Lighter brown
                2 -> Color(0xFF7A4734) // Darker brown
                3 -> Color(0xFFB28268) // Very light brown
                4 -> Color(0xFF583224) // Very dark brown
                else -> Color(
                    (baseColor.red + index * 0.05f).coerceIn(0f, 1f),
                    (baseColor.green + index * 0.03f).coerceIn(0f, 1f),
                    (baseColor.blue + index * 0.02f).coerceIn(0f, 1f)
                )
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Text(
            text = "Today's Breakdown",
            color = CoffeeBrown,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(180.dp)
                .padding(8.dp)
                .weight(0.6f)
        ) {
            // Display center text with the recalculated percentage
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "${dailyBreakdown.totalAmount}",
                    color = CoffeeBrown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Text(
                    text = "mg total",
                    color = CoffeeBrown,
                    fontSize = 12.sp
                )
                Text(
                    text = "${(percentOfLimit * 100).toInt()}%",
                    color = CoffeeBrown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "of limit",
                    color = CoffeeBrown,
                    fontSize = 12.sp
                )
            }

            Canvas(modifier = Modifier.size(180.dp)) {
                val strokeWidth = 20.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Background track
                drawCircle(
                    color = CremeBg,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Draw segments - cap at 100% visual fill
                var startAngle = -90f
                dailyBreakdown.items.forEachIndexed { index, item ->
                    // Calculate this item's percentage of the total intake
                    val itemPercentOfTotal = item.amount.toFloat() / dailyBreakdown.totalAmount

                    // Calculate how much of the circle this item should fill
                    val sweepAngle = itemPercentOfTotal * 360f * (percentOfLimit.coerceAtMost(1f))

                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )

                    startAngle += sweepAngle
                }
            }
        }

        // Legend with colored boxes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .weight(0.4f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dailyBreakdown.items.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors[index % colors.size])
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