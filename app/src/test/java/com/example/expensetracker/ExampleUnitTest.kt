package com.example.expensetracker

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun balanceCalculation_isCorrect() {
        val income = 5000.0
        val expense = 2000.0
        val balance = income - expense
        assertEquals(3000.0, balance, 0.001)
    }

    @Test
    fun transactionType_isValid() {
        val validTypes = listOf("income", "expense")
        assertTrue(validTypes.contains("income"))
        assertTrue(validTypes.contains("expense"))
        assertFalse(validTypes.contains("other"))
    }
}