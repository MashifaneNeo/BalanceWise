package com.example.balancewise.model

data class ExpenseItem(
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val category: String,
    val photoUri: String? = null
)