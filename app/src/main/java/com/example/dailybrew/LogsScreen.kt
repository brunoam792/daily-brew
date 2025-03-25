package com.example.dailybrew

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dailybrew.data.entities.Drink
import com.example.dailybrew.viewmodels.LogsViewModel

@Composable
fun LogsScreen(navController: NavHostController = rememberNavController()) {
    // Get the application context
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication

    // Create ViewModel with factory
    val logsViewModel: LogsViewModel = viewModel(
        factory = LogsViewModel.LogsViewModelFactory(
            application.intakeRepository,
            application.drinkRepository
        )
    )

    // UI state
    val logs by logsViewModel.logEntries.collectAsState()
    val availableDrinks by logsViewModel.availableDrinks.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf<Long?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            successMessage = null
        }
    }

    // Main screen
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with title and add drink button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Caffeine Intake Logs",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = { navController.navigate("addDrink") }
                ) {
                    Text("+ Drink")
                }
            }

            // If no logs, show empty message
            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No caffeine intake records yet.\nTap the + button to add one.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // List of log entries
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        LogEntryCard(
                            logEntry = log,
                            onDeleteClick = { showDeleteConfirmation = log.id }
                        )
                    }
                }
            }
        }

        // Floating action button to add new log
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Intake"
            )
        }

        // Snackbar for messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Space for bottom navigation
        )
    }

    // Dialog to add new intake log
    if (showAddDialog) {
        AddIntakeDialog(
            availableDrinks = availableDrinks,
            onDismiss = { showAddDialog = false },
            onConfirm = { drinkId, servings ->
                logsViewModel.addIntake(drinkId, servings)
                showAddDialog = false
                successMessage = "Intake recorded successfully!"
            }
        )
    }

    // Delete confirmation
    if (showDeleteConfirmation != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Delete Record") },
            text = { Text("Are you sure you want to delete this intake record?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation?.let { logId ->
                            logsViewModel.deleteIntake(logId)
                            successMessage = "Record deleted successfully!"
                        }
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Card to display a log entry
@Composable
fun LogEntryCard(
    logEntry: LogsViewModel.LogEntryUiModel,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Log information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = logEntry.drinkName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${logEntry.amount} mg of caffeine",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = logEntry.formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Dialog to add new intake
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIntakeDialog(
    availableDrinks: List<Drink>,
    onDismiss: () -> Unit,
    onConfirm: (drinkId: Long, servings: Float) -> Unit
) {
    var selectedDrink by remember { mutableStateOf<Drink?>(null) }
    var servings by remember { mutableFloatStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }

    // When we get the drink list, select the first one by default
    LaunchedEffect(availableDrinks) {
        if (availableDrinks.isNotEmpty() && selectedDrink == null) {
            selectedDrink = availableDrinks.first()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Caffeine Intake") },
        text = {
            Column {
                // Drink selection
                Text(
                    text = "Select drink:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        value = selectedDrink?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableDrinks.forEach { drink ->
                            DropdownMenuItem(
                                text = { Text(drink.name) },
                                onClick = {
                                    selectedDrink = drink
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Serving size
                Text(
                    text = "Quantity (servings): ${String.format("%.1f", servings)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = servings,
                    onValueChange = { servings = it },
                    valueRange = 0.5f..3f,
                    steps = 5
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Summary
                selectedDrink?.let { drink ->
                    val totalCaffeine = (drink.caffeinePerServing * servings).toInt()
                    Text(
                        text = "Total caffeine: $totalCaffeine mg",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Volume: ${(drink.servingSize * servings).toInt()} ml",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedDrink?.let { drink ->
                        onConfirm(drink.drinkId, servings)
                    }
                },
                enabled = selectedDrink != null
            ) {
                Text("Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}