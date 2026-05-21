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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FitnessMetric
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(viewModel: HealthViewModel) {
    val metrics by viewModel.fitnessMetrics.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Metrics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Metric")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (metrics.isNotEmpty()) {
                WeightChart(metrics = metrics)
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text("Recent Logs", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(metrics) { metric ->
                    MetricItem(metric = metric, onDelete = { viewModel.deleteFitnessMetric(it) })
                }
            }
        }
    }

    if (showAddDialog) {
        var weight by remember { mutableStateOf("") }
        var muscleMass by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Metrics") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = muscleMass,
                        onValueChange = { muscleMass = it },
                        label = { Text("Muscle Mass (kg) optional") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val w = weight.toFloatOrNull() ?: 0f
                    val m = muscleMass.toFloatOrNull() ?: 0f
                    if (w > 0) {
                        viewModel.addFitnessMetric(w, m)
                    }
                    showAddDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MetricItem(metric: FitnessMetric, onDelete: (FitnessMetric) -> Unit) {
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
                Text("${metric.weight} kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                if (metric.muscleMass > 0) {
                    Text("Muscle: ${metric.muscleMass} kg", style = MaterialTheme.typography.bodyMedium)
                }
                val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                Text(format.format(Date(metric.timestamp)), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onDelete(metric) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun WeightChart(metrics: List<FitnessMetric>) {
    val sorted = metrics.sortedBy { it.timestamp }
    if (sorted.isEmpty()) return

    val maxWeight = sorted.maxOf { it.weight }
    val minWeight = sorted.minOf { it.weight }.let { if (it == maxWeight) it - 1f else it }
    
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weight Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val width = size.width
                val height = size.height
                val padding = 16.dp.toPx()
                
                val drawWidth = width - 2 * padding
                val drawHeight = height - 2 * padding
                
                val rangeWeight = if (maxWeight > minWeight) maxWeight - minWeight else 1f
                val stepX = if (sorted.size > 1) drawWidth / (sorted.size - 1) else drawWidth

                val path = Path()
                
                sorted.forEachIndexed { index, metric ->
                    val x = padding + index * stepX
                    val y = padding + drawHeight - ((metric.weight - minWeight) / rangeWeight) * drawHeight
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = Offset(x, y))
                }
                
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}
