package com.example.balancewise

import com.example.balancewise.database.Expense
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ExpenseTest {

    @Test
    fun testCreateExpense_WithValidData_ShouldSucceed() {
        val expense = Expense(
            id = 1,
            amount = 250.50,
            date = "29/04/2026",
            startTime = "14:30",
            endTime = "15:30",
            description = "Lunch with team",
            categoryId = 1,
            userId = 1,
            photoUri = null,
            currency = "ZAR",
            recurringType = "None"
        )

        //Verification of expenses being created correctly
        assertNotNull(expense)
        assertEquals(250.50, expense.amount, 0.01)
        assertEquals("Lunch with team", expense.description)
        assertEquals("ZAR", expense.currency)
        assertEquals("None", expense.recurringType)
        assertEquals(1, expense.categoryId)
    }

    @Test
    fun testExpenseAmount_ShouldBePositive() {
        //Expenses with positive amount
        val validExpense = Expense(
            id = 1,
            amount = 100.00,
            date = "29/04/2026",
            startTime = "10:00",
            endTime = "11:00",
            description = "Groceries",
            categoryId = 1,
            userId = 1,
            photoUri = null,
            currency = "ZAR",
            recurringType = "None"
        )

        //The amount should be positive
        assertTrue(validExpense.amount > 0)
        assertTrue(validExpense.amount == 100.00)
    }
}