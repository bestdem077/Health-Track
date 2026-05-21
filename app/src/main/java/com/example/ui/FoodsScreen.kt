package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CustomFood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodsScreen(viewModel: HealthViewModel) {
    val customFoods by viewModel.availableFoods.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<CustomFood?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Templates", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                foodToEdit = null
                showAddDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Food")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(customFoods) { food ->
                    CustomFoodItem(
                        food = food,
                        onEdit = { 
                            foodToEdit = it
                            showAddDialog = true 
                        },
                        onDelete = { viewModel.deleteCustomFood(it) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomFoodDialog(
            foodToEdit = foodToEdit,
            onDismiss = { 
                showAddDialog = false
                foodToEdit = null
            },
            onAdd = { name, cals, p, c, f ->
                if (foodToEdit != null) {
                    viewModel.updateCustomFood(foodToEdit!!.copy(name = name, calories = cals, protein = p, carbs = c, fat = f))
                } else {
                    viewModel.addCustomFood(name, cals, p, c, f)
                }
                showAddDialog = false
                foodToEdit = null
            }
        )
    }
}

@Composable
fun CustomFoodItem(food: CustomFood, onEdit: (CustomFood) -> Unit, onDelete: (CustomFood) -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(food.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("${food.calories} kcal • P:${food.protein}g C:${food.carbs}g F:${food.fat}g", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = { onEdit(food) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(food) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddCustomFoodDialog(
    foodToEdit: CustomFood?,
    onDismiss: () -> Unit,
    onAdd: (String, Int, Float, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf(foodToEdit?.name ?: "") }
    var calories by remember { mutableStateOf(foodToEdit?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(foodToEdit?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(foodToEdit?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(foodToEdit?.fat?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (foodToEdit != null) "Edit Custom Food" else "Add Custom Food") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") }
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
