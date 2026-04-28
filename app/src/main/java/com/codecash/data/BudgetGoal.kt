package com.codecash.data

data class BudgetGoal(
    val id: Int,
    val userId: Int,
    val categoryId: Int,
    val minAmount: Double,
    val maxAmount: Double,
    val monthYear: String
)