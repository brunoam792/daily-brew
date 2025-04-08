package com.example.dailybrew

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailybrew.ui.theme.CoffeeBrown
import com.example.dailybrew.ui.theme.CremeBg
import com.example.dailybrew.viewmodels.LimitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitScreen() {
    // Get the application context
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication

    // Create the ViewModel with the factory
    val limitViewModel: LimitViewModel = viewModel(
        factory = LimitViewModel.LimitViewModelFactory(
            application.dailyLimitRepository
        )
    )

    // Collect the ViewModel state
    val inputLimit by limitViewModel.inputLimit.collectAsState()
    val currentLimit by limitViewModel.currentLimit.collectAsState()
    val saveSuccess by limitViewModel.saveSuccess.collectAsState()

    // State for the Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Show a message when saving is successful
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Daily limit saved successfully!")
        }
    }

    // UI implementation with Box as root container
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Text(
                text = "",
                style = MaterialTheme.typography.headlineMedium,
                color = CoffeeBrown,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Main content card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CremeBg
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set your daily caffeine limit",
                        color = CoffeeBrown,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Text field com label e hint - Corrigido para não usar outlinedTextFieldColors
                    OutlinedTextField(
                        value = inputLimit,
                        onValueChange = { limitViewModel.updateInputLimit(it) },
                        label = { Text("Limit (mg)", color = CoffeeBrown) },
                        supportingText = { Text("The recommended daily limit is 400mg", color = CoffeeBrown) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Information about the current limit
                    currentLimit?.let {
                        Text(
                            text = "Your current limit: $it mg",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CoffeeBrown
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Save button
                    Button(
                        onClick = { limitViewModel.saveLimit() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoffeeBrown,
                            contentColor = CremeBg
                        ),
                        enabled = inputLimit.isNotEmpty() &&
                                inputLimit.toIntOrNull() != null &&
                                inputLimit.toIntOrNull() != currentLimit
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Save")
                    }
                }
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = CoffeeBrown,
                contentColor = CremeBg,
                content = { Text(data.visuals.message) }
            )
        }
    }
}