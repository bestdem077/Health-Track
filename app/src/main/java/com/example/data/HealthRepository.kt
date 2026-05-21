package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class HealthRepository(private val healthDao: HealthDao) {
    
    val allCustomFoods = healthDao.getAllCustomFoods()
    val allFoodLogs = healthDao.getAllFoodLogs()
    val allFitnessMetrics = healthDao.getAllFitnessMetrics()

    fun getFoodLogsForDay(timestamp: Long): Flow<List<FoodLogEntry>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis - 1
        
        return healthDao.getFoodLogsBetween(startOfDay, endOfDay)
    }

    suspend fun insertCustomFood(food: CustomFood) = healthDao.insertCustomFood(food)
    suspend fun deleteCustomFood(food: CustomFood) = healthDao.deleteCustomFood(food)

    suspend fun insertFoodLog(log: FoodLogEntry) = healthDao.insertFoodLog(log)
    suspend fun deleteFoodLog(log: FoodLogEntry) = healthDao.deleteFoodLog(log)

    suspend fun insertFitnessMetric(metric: FitnessMetric) = healthDao.insertFitnessMetric(metric)
    suspend fun deleteFitnessMetric(metric: FitnessMetric) = healthDao.deleteFitnessMetric(metric)
}
