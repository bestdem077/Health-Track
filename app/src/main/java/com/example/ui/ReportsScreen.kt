package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: HealthViewModel) {
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
    
    // Process logs for last 7 days
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    
    val groupedByDay = mutableMapOf<String, Int>()
    
    // Initialize last 7 days
    for (i in 0..6) {
        val dateCal = cal.clone() as Calendar
        dateCal.add(Calendar.DAY_OF_YEAR, -i)
        val dateString = SimpleDateFormat("MMM dd", Locale.getDefault()).format(dateCal.time)
        groupedByDay[dateString] = 0
    }
    
    foodLogs.forEach { log ->
        val logDate = Calendar.getInstance().apply { timeInMillis = log.timestamp }
        val dateString = SimpleDateFormat("MMM dd", Locale.getDefault()).format(logDate.time)
        if (groupedByDay.containsKey(dateString)) {
            groupedByDay[dateString] = groupedByDay.getOrDefault(dateString, 0) + log.calories
        }
    }

    val chartData = groupedByDay.entries.sortedByDescending { it.key }.reversed().toList()
    val maxCals = chartData.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Digest", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Caloric Intake (Last 7 Days)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val primaryColor = MaterialTheme.colorScheme.primary
                    
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                    ) {
                        val barWidth = size.width / (chartData.size * 2)
                        val spaceWidth = size.width / (chartData.size * 2)
                        
                        chartData.forEachIndexed { index, entry ->
                            val x = index * (barWidth + spaceWidth) + spaceWidth / 2
                            val heightRatio = entry.value.toFloat() / maxCals.toFloat()
                            val barHeight = size.height * heightRatio
                            val y = size.height - barHeight
                            
                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            chartData.forEach { (dateStr, cals) ->
                 Card(
                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                     shape = MaterialTheme.shapes.medium,
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                     elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                 ) {
                     Row(
                         modifier = Modifier.fillMaxWidth().padding(16.dp),
                         horizontalArrangement = Arrangement.SpaceBetween
                     ) {
                         Text(dateStr, style = MaterialTheme.typography.bodyMedium)
                         Text("$cals kcal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                     }
                 }
            }
        }
    }
}
