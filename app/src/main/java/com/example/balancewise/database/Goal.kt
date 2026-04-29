package com.example.balancewise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val minGoal: Double,
    val maxGoal: Double
)