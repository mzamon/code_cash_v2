package com.codecash.data

import android.graphics.Color

data class Category(
    val id: Int,
    val name: String,
    val colorHex: String = "#2dd4bf",
    val minBudget: Double = 0.0,
    val maxBudget: Double = 0.0,
    val iconName: String = "default"
) {
    fun getColorInt(): Int = Color.parseColor(colorHex)
}