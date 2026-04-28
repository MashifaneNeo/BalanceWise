package com.example.balancewise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId LIMIT 1")
    suspend fun getGoalForUser(userId: Int): Goal?
}