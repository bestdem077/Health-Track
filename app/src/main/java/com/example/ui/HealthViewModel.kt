package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CustomFood
import com.example.data.FitnessMetric
import com.example.data.FoodLogEntry
import com.example.data.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class HealthViewModel(private val application: Application, private val repository: HealthRepository) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("health_prefs", Context.MODE_PRIVATE)

    init {
        val defaultsAdded = prefs.getBoolean("defaults_added", false)
        if (!defaultsAdded) {
            viewModelScope.launch {
                val defaultFoods = listOf(
                    CustomFood(name = "Apple (1 medium)", calories = 95, protein = 0.5f, carbs = 25f, fat = 0.3f),
                    CustomFood(name = "Banana (1 medium)", calories = 105, protein = 1.3f, carbs = 27f, fat = 0.3f),
                    CustomFood(name = "Chicken Breast (100g)", calories = 165, protein = 31f, carbs = 0f, fat = 3.6f),
                    CustomFood(name = "White Rice (1 cup)", calories = 205, protein = 4.3f, carbs = 45f, fat = 0.4f),
                    CustomFood(name = "Egg (1 large)", calories = 78, protein = 6.3f, carbs = 0.6f, fat = 5.3f),
                    CustomFood(name = "Oatmeal (1 cup)", calories = 158, protein = 6f, carbs = 27f, fat = 3.2f),
                    CustomFood(name = "Almonds (1 oz)", calories = 164, protein = 6f, carbs = 6f, fat = 14f)
                )
                defaultFoods.forEach { repository.insertCustomFood(it) }
                prefs.edit().putBoolean("defaults_added", true).apply()
            }
        }
    }

    val availableFoods: StateFlow<List<CustomFood>> = repository.allCustomFoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodLogs: StateFlow<List<FoodLogEntry>> = repository.allFoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fitnessMetrics: StateFlow<List<FitnessMetric>> = repository.allFitnessMetrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCustomFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.insertCustomFood(CustomFood(name = name, calories = calories, protein = protein, carbs = carbs, fat = fat))
        }
    }

    fun updateCustomFood(food: CustomFood) {
        viewModelScope.launch {
            repository.insertCustomFood(food)
        }
    }

    fun deleteCustomFood(food: CustomFood) {
        viewModelScope.launch {
            repository.deleteCustomFood(food)
        }
    }

    fun addFoodLog(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.insertFoodLog(FoodLogEntry(name = name, calories = calories, protein = protein, carbs = carbs, fat = fat))
        }
    }
    
    fun deleteFoodLog(log: FoodLogEntry) {
        viewModelScope.launch {
            repository.deleteFoodLog(log)
        }
    }

    fun addFitnessMetric(weight: Float, muscleMass: Float) {
        viewModelScope.launch {
            repository.insertFitnessMetric(FitnessMetric(weight = weight, muscleMass = muscleMass))
        }
    }
    
    fun deleteFitnessMetric(metric: FitnessMetric) {
         viewModelScope.launch {
            repository.deleteFitnessMetric(metric)
        }
    }
}

class HealthViewModelFactory(private val application: Application, private val repository: HealthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
