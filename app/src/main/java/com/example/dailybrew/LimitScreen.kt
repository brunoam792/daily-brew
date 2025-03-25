package com.example.dailybrew

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailybrew.viewmodels.LimitViewModel

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
                .padding(16.dp)
        ) {
            Text(
                text = "Set your daily caffeine limit",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text field with explanation
            OutlinedTextField(
                value = inputLimit,
                onValueChange = { limitViewModel.updateInputLimit(it) },
                label = { Text("Limit (mg)") },
                supportingText = { Text("The recommended daily limit is 400mg") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Information about the current limit
            currentLimit?.let {
                Text(
                    text = "Your current limit: $it mg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = { limitViewModel.saveLimit() },
                modifier = Modifier.align(Alignment.End),
                enabled = inputLimit.isNotEmpty() && inputLimit.toIntOrNull() != null && inputLimit.toIntOrNull() != currentLimit
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Save",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Save")
            }
        }

        // Snackbar host - agora posicionado no Box
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                content = { Text(data.visuals.message) }
            )
        }
    }
}