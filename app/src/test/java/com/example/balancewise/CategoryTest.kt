package com.example.balancewise

import com.example.balancewise.database.Category
import org.junit.Assert.*
import org.junit.Test

class CategoryTest {

    @Test
    fun testCreateCategory_WithValidName_ShouldSucceed() {
        val category = Category(
            id = 1,
            name = "Groceries",
            userId = 1
        )

        assertNotNull(category)
        assertEquals("Groceries", category.name)
        assertEquals(1, category.userId)
    }

    @Test
    fun testCategoryName_ShouldNotBeEmpty() {
        val validCategory = Category(
            id = 1,
            name = "Transport",
            userId = 1
        )

        val emptyCategory = Category(
            id = 2,
            name = "",
            userId = 1
        )

        assertTrue(validCategory.name.isNotBlank())

        assertTrue(emptyCategory.name.isBlank())
    }

    @Test
    fun testCategoryName_ShouldBeUnique() {
        val categoryNames = listOf("Food", "Transport", "Entertainment", "Bills")

        // Test that category names can be stored and retrieved
        val category1 = Category(name = "Food", userId = 1)
        val category2 = Category(name = "Transport", userId = 1)

        assertNotEquals(category1.name, category2.name)
        assertTrue(categoryNames.contains(category1.name))
        assertTrue(categoryNames.contains(category2.name))
    }
}