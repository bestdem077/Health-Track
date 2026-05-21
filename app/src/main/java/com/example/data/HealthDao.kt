package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    // Custom Foods
    @Query("SELECT * FROM custom_foods ORDER BY name ASC")
    fun getAllCustomFoods(): Flow<List<CustomFood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomFood(food: CustomFood)

    @Delete
    suspend fun deleteCustomFood(food: CustomFood)

    // Food Logs
    @Query("SELECT * FROM food_logs ORDER BY timestamp DESC")
    fun getAllFoodLogs(): Flow<List<FoodLogEntry>>

    @Query("SELECT * FROM food_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getFoodLogsBetween(startTime: Long, endTime: Long): Flow<List<FoodLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(log: FoodLogEntry)

    @Delete
    suspend fun deleteFoodLog(log: FoodLogEntry)

    // Fitness Metrics
    @Query("SELECT * FROM fitness_metrics ORDER BY timestamp DESC")
    fun getAllFitnessMetrics(): Flow<List<FitnessMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFitnessMetric(metric: FitnessMetric)

    @Delete
    suspend fun deleteFitnessMetric(metric: FitnessMetric)
}
