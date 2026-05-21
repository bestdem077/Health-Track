package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.HealthDatabase
import com.example.data.HealthRepository
import com.example.ui.AppNavigation
import com.example.ui.HealthViewModel
import com.example.ui.HealthViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = HealthDatabase.getDatabase(this)
        val repository = HealthRepository(database.healthDao())
        
        setContent {
            val viewModel: HealthViewModel = viewModel(
                factory = HealthViewModelFactory(this.application, repository)
            )
            MyApplicationTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

