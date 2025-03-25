package com.example.dailybrew

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dailybrew.data.entities.Drink
import com.example.dailybrew.data.repositories.DrinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrinkScreen(navController: NavController) {
    // UI state
    var drinkName by remember { mutableStateOf("") }
    var caffeinePerServing by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var caffeineError by remember { mutableStateOf<String?>(null) }
    var servingSizeError by remember { mutableStateOf<String?>(null) }

    // Context to access repository
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication
    val drinkRepository = application.drinkRepository

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Drink") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drink name field
            OutlinedTextField(
                value = drinkName,
                onValueChange = {
                    drinkName = it
                    nameError = if (it.isBlank()) "Name cannot be empty" else null
                },
                label = { Text("Drink name") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Caffeine amount field
            OutlinedTextField(
                value = caffeinePerServing,
                onValueChange = {
                    caffeinePerServing = it
                    caffeineError = when {
                        it.isBlank() -> "Caffeine amount cannot be empty"
                        it.toIntOrNull() == null -> "Enter a valid number"
                        it.toInt() <= 0 -> "Amount must be greater than zero"
                        else -> null
                    }
                },
                label = { Text("Caffeine per serving (mg)") },
                isError = caffeineError != null,
                supportingText = caffeineError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Serving size field
            OutlinedTextField(
                value = servingSize,
                onValueChange = {
                    servingSize = it
                    servingSizeError = when {
                        it.isBlank() -> "Serving size cannot be empty"
                        it.toIntOrNull() == null -> "Enter a valid number"
                        it.toInt() <= 0 -> "Size must be greater than zero"
                        else -> null
                    }
                },
                label = { Text("Serving size (ml)") },
                isError = servingSizeError != null,
                supportingText = servingSizeError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = {
                    // Validate fields
                    nameError = if (drinkName.isBlank()) "Name cannot be empty" else null
                    caffeineError = when {
                        caffeinePerServing.isBlank() -> "Caffeine amount cannot be empty"
                        caffeinePerServing.toIntOrNull() == null -> "Enter a valid number"
                        caffeinePerServing.toInt() <= 0 -> "Amount must be greater than zero"
                        else -> null
                    }
                    servingSizeError = when {
                        servingSize.isBlank() -> "Serving size cannot be empty"
                        servingSize.toIntOrNull() == null -> "Enter a valid number"
                        servingSize.toInt() <= 0 -> "Size must be greater than zero"
                        else -> null
                    }

                    // If no errors, save
                    if (nameError == null && caffeineError == null && servingSizeError == null) {
                        isLoading = true
                        val newDrink = Drink(
                            name = drinkName,
                            caffeinePerServing = caffeinePerServing.toInt(),
                            servingSize = servingSize.toInt()
                        )

                        // Save to database
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val id = drinkRepository.insert(newDrink)
                                if (id > 0) {
                                    snackbarHostState.showSnackbar("Drink added successfully!")
                                    // Clear fields
                                    drinkName = ""
                                    caffeinePerServing = ""
                                    servingSize = ""
                                    // Navigate back after delay
                                    isLoading = false
                                    delay(1500)
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error adding drink: ${e.message}")
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    Text("Saving...")
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Save Drink")
                }
            }

            // Additional information
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Examples of caffeine per serving:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Espresso (30ml): 63mg\n" +
                        "• Americano (240ml): 95mg\n" +
                        "• Cappuccino (120ml): 63mg\n" +
                        "• Black Tea (240ml): 47mg\n" +
                        "• Green Tea (240ml): 28mg\n" +
                        "• Energy Drink (250ml): 80mg",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

// Function for delay
private suspend fun delay(timeMillis: Long) {
    kotlinx.coroutines.delay(timeMillis)
}