package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "custom_foods")
data class CustomFood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
)

@Serializable
@Entity(tableName = "food_logs")
data class FoodLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "fitness_metrics")
data class FitnessMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weight: Float = 0f,
    val muscleMass: Float = 0f,
    val otherMetrics: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
