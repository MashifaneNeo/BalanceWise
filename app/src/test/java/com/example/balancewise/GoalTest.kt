package com.example.balancewise

import com.example.balancewise.database.Goal
import org.junit.Assert.*
import org.junit.Test

class GoalTest {

    @Test
    fun testCreateGoal_WithValidValues_ShouldSucceed() {
        val goal = Goal(
            id = 1,
            userId = 1,
            minGoal = 500.0,
            maxGoal = 5000.0
        )

        assertNotNull(goal)
        assertEquals(500.0, goal.minGoal, 0.01)
        assertEquals(5000.0, goal.maxGoal, 0.01)
        assertEquals(1, goal.userId)
    }


    @Test
    fun testGoalProgress() {
        val minGoal = 500.0
        val maxGoal = 5000.0
        val currentSpending = 2500.0

        val progress = ((currentSpending - minGoal) / (maxGoal - minGoal)) * 100

        // Progress should be between 0 and 100
        assertTrue(progress in 0.0..100.0)
        assertEquals(44.44, progress, 1.0)
    }

}