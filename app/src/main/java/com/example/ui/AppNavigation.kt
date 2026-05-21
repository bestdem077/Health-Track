package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FitnessMetric
import com.example.data.FoodLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: HealthViewModel) {
    val items = listOf(
        NavigationItem("diet", "Daily", Icons.Default.Restaurant),
        NavigationItem("metrics", "Metrics", Icons.Default.FitnessCenter),
        NavigationItem("foods", "Foods", Icons.Default.List),
        NavigationItem("reports", "Reports", Icons.Default.BarChart)
    )
    var selectedItem by remember { mutableStateOf(items[0]) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        modifier = Modifier.testTag("nav_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem.route) {
                "diet" -> DietScreen(viewModel)
                "metrics" -> MetricsScreen(viewModel)
                "foods" -> FoodsScreen(viewModel)
                "reports" -> ReportsScreen(viewModel)
            }
        }
    }
}

data class NavigationItem(val route: String, val title: String, val icon: ImageVector)
