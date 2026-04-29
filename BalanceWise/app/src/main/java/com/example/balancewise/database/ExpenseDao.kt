package com.example.balancewise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    // 📋 Get all expenses for a user
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId
    """)
    suspend fun getExpensesForUser(userId: Int): List<Expense>

    // 📅 Date range filter
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

    // 🔍 Search only
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId
        AND LOWER(description) LIKE '%' || LOWER(:query) || '%'
    """)
    suspend fun searchExpenses(
        userId: Int,
        query: String
    ): List<Expense>

    // 🔍 + 📅 Search + Date
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId
        AND LOWER(description) LIKE '%' || LOWER(:query) || '%'
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun searchExpensesByDate(
        userId: Int,
        query: String,
        startDate: String,
        endDate: String
    ): List<Expense>

    // 💰 Category totals
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