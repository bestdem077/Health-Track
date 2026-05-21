package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CustomFood
import com.example.data.FoodLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(viewModel: HealthViewModel) {
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Filter today's logs
    val todayLogs = foodLogs.filter { isToday(it.timestamp) }
    val totalCalories = todayLogs.sumOf { it.calories }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Log", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_log_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Food")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Today's Calories", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$totalCalories kcal",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Text("Food Entries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(todayLogs) { log ->
                    FoodLogItem(log = log, onDelete = { viewModel.deleteFoodLog(it) })
                }
            }
        }
    }

    if (showAddDialog) {
        val availableFoods by viewModel.availableFoods.collectAsStateWithLifecycle()
        AddFoodDialog(
            customFoods = availableFoods,
            onDismiss = { showAddDialog = false },
            onAdd = { name, cals, p, c, f ->
                viewModel.addFoodLog(name, cals, p, c, f)
                showAddDialog = false
            },
            onSaveCustomFood = { name, cals, p, c, f ->
                viewModel.addCustomFood(name, cals, p, c, f)
            }
        )
    }
}

@Composable
fun FoodLogItem(log: FoodLogEntry, onDelete: (FoodLogEntry) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(log.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                Text(format.format(Date(log.timestamp)), style = MaterialTheme.typography.bodySmall)
                Text("P:${log.protein}g C:${log.carbs}g F:${log.fat}g", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${log.calories} kcal", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onDelete(log) }, modifier = Modifier.testTag("delete_log_${log.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(
    customFoods: List<CustomFood>,
    onDismiss: () -> Unit,
    onAdd: (String, Int, Float, Float, Float) -> Unit,
    onSaveCustomFood: (String, Int, Float, Float, Float) -> Unit
) {
    var selectedFood by remember { mutableStateOf<CustomFood?>(null) }
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var saveAsCustom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Food") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                
                // Allow Custom Food Selection
                if (customFoods.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFood?.name ?: "Select Custom Food (Optional)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Recent Foods") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear Selection") },
                                onClick = { 
                                    selectedFood = null
                                    name = ""
                                    calories = ""
                                    protein = ""
                                    carbs = ""
                                    fat = ""
                                    expanded = false
                                }
                            )
                            customFoods.forEach { food ->
                                DropdownMenuItem(
                                    text = { Text(food.name) },
                                    onClick = {
                                        selectedFood = food
                                        name = food.name
                                        calories = food.calories.toString()
                                        protein = food.protein.toString()
                                        carbs = food.carbs.toString()
                                        fat = food.fat.toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.testTag("food_name_input")
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.testTag("food_cals_input")
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                     OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (selectedFood == null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = saveAsCustom, onCheckedChange = { saveAsCustom = it })
                        Text("Save as custom food template")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val cal = calories.toIntOrNull() ?: 0
                val p = protein.toFloatOrNull() ?: 0f
                val c = carbs.toFloatOrNull() ?: 0f
                val f = fat.toFloatOrNull() ?: 0f
                if (name.isNotBlank()) {
                    onAdd(name, cal, p, c, f)
                    if (saveAsCustom) {
                        onSaveCustomFood(name, cal, p, c, f)
                    }
                }
            }) {
                Text("Log Food")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun isToday(timestamp: Long): Boolean {
    val logDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    return logDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
           logDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}
