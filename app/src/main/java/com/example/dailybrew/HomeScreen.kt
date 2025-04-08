package com.example.dailybrew

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailybrew.data.entities.Intake
import com.example.dailybrew.ui.theme.CoffeeBrown
import com.example.dailybrew.ui.theme.CremeBg
import com.example.dailybrew.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    // Get the application context
    val context = LocalContext.current
    val application = context.applicationContext as DailyBrewApplication
    val scope = rememberCoroutineScope()

    // Create the ViewModel with the factory
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(
            application.intakeRepository,
            application.dailyLimitRepository
        )
    )

    // Collect the ViewModel state
    val caffeineStatus by homeViewModel.caffeineStatus.collectAsState()

    // State for the drink carousel
    val drinks = remember { listOf("Espresso", "Latte", "Drip") }
    var currentDrinkIndex by remember { mutableStateOf(0) }
    val currentDrink = drinks[currentDrinkIndex]

    // Get the drink from the repository
    val drinkRepository = application.drinkRepository
    val intakeRepository = application.intakeRepository

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Main content
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Espaçador maior no topo para dar mais espaço
            Spacer(modifier = Modifier.height(80.dp))

            // Card restaurado para as informações de consumo - com cores consistentes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CremeBg // Cor de fundo creme
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp // Leve elevação para destacar o card
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Your daily Intake",
                        color = CoffeeBrown,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Caffeine amount / limit
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${caffeineStatus.currentAmount}mg",
                            color = getIntakeColor(caffeineStatus.percentOfLimit),
                            fontSize = 32.sp,  // Texto maior para o valor
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " / ${caffeineStatus.dailyLimit}mg",
                            color = CoffeeBrown,
                            fontSize = 32.sp  // Texto maior para o valor
                        )
                    }
                }
            }

            // Spacer maior para melhor distribuição vertical
            Spacer(modifier = Modifier.height(40.dp))

            // Drink selection carousel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Previous button - ícone maior
                IconButton(
                    onClick = {
                        currentDrinkIndex = if (currentDrinkIndex > 0)
                            currentDrinkIndex - 1 else drinks.size - 1
                    },
                    modifier = Modifier.size(64.dp) // Botão maior
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous drink",
                        tint = CoffeeBrown,
                        modifier = Modifier.size(64.dp) // Ícone maior
                    )
                }

                // Current drink image - imagem maior
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(180.dp) // Container maior
                ) {
                    // Determine image resource based on current drink
                    val imageResource = when (currentDrink) {
                        "Espresso" -> R.drawable.espresso
                        "Latte" -> R.drawable.latte
                        "Drip" -> R.drawable.drip
                        else -> R.drawable.espresso
                    }

                    // Display the drink image - imagem muito maior
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = currentDrink,
                        modifier = Modifier.size(150.dp) // Ícone muito maior
                    )
                }

                // Next button - ícone maior
                IconButton(
                    onClick = {
                        currentDrinkIndex = (currentDrinkIndex + 1) % drinks.size
                    },
                    modifier = Modifier.size(64.dp) // Botão maior
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next drink",
                        tint = CoffeeBrown,
                        modifier = Modifier.size(64.dp) // Ícone maior
                    )
                }
            }

            // Type of drink text - fonte maior
            Text(
                text = currentDrink,
                color = CoffeeBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp, // Texto maior
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Add button for current drink
            Button(
                onClick = {
                    // Logic to add the selected drink
                    scope.launch {
                        val drinkToAdd = when (currentDrink) {
                            "Espresso" -> 1L // IDs from database
                            "Latte" -> 3L
                            "Drip" -> 2L
                            else -> 1L
                        }

                        try {
                            // Get the drink from repository
                            var drinkFound = false
                            drinkRepository.getDrinkById(drinkToAdd).collect { drink ->
                                if (drink != null && !drinkFound) {
                                    drinkFound = true
                                    // Add intake
                                    val intake = Intake(
                                        userId = 1L, // Default user
                                        drinkId = drink.drinkId,
                                        servings = 1f,
                                        totalCaffeine = drink.caffeinePerServing
                                    )
                                    intakeRepository.insert(intake)
                                    snackbarHostState.showSnackbar("Added ${drink.name}")
                                }
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error: ${e.message}")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoffeeBrown
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(56.dp)  // Botão mais alto
                    .width(200.dp)  // Botão mais largo
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp)  // Ícone maior
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentDrink,
                    fontSize = 18.sp  // Texto maior no botão
                )
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Space for nav bar
        )
    }
}

// Helper function to get color based on percentage of limit
@Composable
private fun getIntakeColor(percentOfLimit: Float): Color {
    return when {
        percentOfLimit >= 1.0f -> Color.Red
        percentOfLimit >= 0.7f -> Color(0xFFFFA000) // Orange
        else -> CoffeeBrown
    }
}