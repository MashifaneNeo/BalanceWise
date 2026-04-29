package com.example.balancewise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId")
    suspend fun getExpensesForUser(userId: Int): List<Expense>

    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId 
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun getExpensesByDateRange(
        userId: Int,
        startDate: String,
        endDate: String
    ): List<Expense>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE userId = :userId 
        AND categoryId = :categoryId
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun getTotalForCategory(
        userId: Int,
        categoryId: Int,
        startDate: String,
        endDate: String
    ): Double?
}