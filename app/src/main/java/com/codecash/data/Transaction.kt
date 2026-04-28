package com.codecash.data

data class Transaction(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val description: String,
    val categoryId: Int,
    val date: Long,
    val photoPath: String? = null,
    val isIncome: Boolean = false
) {
    fun getFormattedAmount(): String = if (isIncome)
        "+R${String.format("%.2f", amount)}"
    else
        "-R${String.format("%.2f", amount)}"

    fun getDateString(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(date))
    }
}