package com.codecash.data

import android.util.Log
import java.util.*

/**
 * Global data store using parallel-style lists to persist data during the app session.
 * This satisfies the requirement for global data access and parallel arrays in Part 2.
 */
object DataStore {
    private const val TAG = "DataStore"

    // Current logged in user ID (-1 = none)
    var currentUserId: Int = -1

    // Parallel arrays for Users
    val userIds = ArrayList<Int>()
    val userNames = ArrayList<String>()
    val userEmails = ArrayList<String>()
    val userPasswords = ArrayList<String>()

    // Parallel arrays for Categories
    val categoryIds = ArrayList<Int>()
    val categoryNames = ArrayList<String>()
    val categoryColors = ArrayList<String>()
    val categoryMinBudgets = ArrayList<Double>()
    val categoryMaxBudgets = ArrayList<Double>()

    // Parallel arrays for Transactions (Extended with start/end times for rubric compliance)
    val transactionIds = ArrayList<Int>()
    val transactionUserIds = ArrayList<Int>()
    val transactionAmounts = ArrayList<Double>()
    val transactionDescriptions = ArrayList<String>()
    val transactionCategoryIds = ArrayList<Int>()
    val transactionDates = ArrayList<Long>()
    val transactionStartTimes = ArrayList<Long>()
    val transactionEndTimes = ArrayList<Long>()
    val transactionPhotoPaths = ArrayList<String?>()
    val transactionIsIncome = ArrayList<Boolean>()

    // Parallel arrays for Budget Goals
    val budgetGoalIds = ArrayList<Int>()
    val budgetGoalUserIds = ArrayList<Int>()
    val budgetGoalCategoryIds = ArrayList<Int>()
    val budgetGoalMinAmounts = ArrayList<Double>()
    val budgetGoalMaxAmounts = ArrayList<Double>()
    val budgetGoalMonthYears = ArrayList<String>()

    // Auto-increment IDs
    private var nextUserId = 1
    private var nextCategoryId = 1
    private var nextTransactionId = 1
    private var nextBudgetGoalId = 1
    
    init {
        initializeTestData()
    }

    private fun initializeTestData() {
        Log.d(TAG, "Initializing test data...")
        
        // 1. Seed Users (Group members)
        addUser("Tshiamo Keefelakae Lentswe", "st10448558@codecash.com", "st10448558")
        addUser("Yinhla Maringa", "st10441743@codecash.com", "st10441743")
        addUser("Matshidiso Nthebe", "st10449727@codecash.com", "st10449727")
        addUser("Mzamo Richmond Ndlovu", "st10455453@codecash.com", "st10455453")

        // 2. Seed Default Categories
        addCategory("Food & Dining", "#22c55e")
        addCategory("Transport", "#3b82f6")
        addCategory("Rent", "#a855f7")
        addCategory("Entertainment", "#f97316")
        addCategory("Utilities", "#ef4444")
        addCategory("Shopping", "#ec4899")

        // 3. Seed Transactions
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        // Sample transactions for Tshiamo (User 1)
        addTransaction(1, 79.99, "Starbucks Coffee", 1, now, now, now + 1800000, null, false)
        addTransaction(1, 7895.00, "Monthly Rent", 3, now - 86400000, now, now, null, false)
        addTransaction(1, 32900.00, "Monthly Salary", 1, now - 172800000, now, now, null, true)
        
        // Set some initial budget goals for User 1
        val currentMonth = getCurrentMonthYear()
        addBudgetGoal(1, 0, 10000.0, 25000.0, currentMonth) // Overall
        addBudgetGoal(1, 1, 500.0, 2000.0, currentMonth)   // Food
        
        Log.d(TAG, "Test data initialized.")
    }

    // ==================== USER OPERATIONS ====================

    fun addUser(name: String, email: String, password: String): Int {
        val id = nextUserId++
        userIds.add(id)
        userNames.add(name)
        userEmails.add(email)
        userPasswords.add(password)
        return id
    }

    /**
     * Requirement: Binary Search for optimized lookup.
     * We'll search through user emails. Note: for binary search, data must be sorted.
     */
    fun findUserByEmail(email: String): Int {
        // In a real implementation with parallel arrays, we'd need to sort all arrays by email.
        // For the sake of the rubric, we'll demonstrate a sorted search logic.
        val indices = userEmails.indices.sortedBy { userEmails[it].lowercase() }
        
        var low = 0
        var high = indices.size - 1
        val target = email.lowercase()

        while (low <= high) {
            val mid = (low + high) / 2
            val midEmail = userEmails[indices[mid]].lowercase()
            
            when {
                midEmail == target -> return userIds[indices[mid]]
                midEmail < target -> low = mid + 1
                else -> high = mid - 1
            }
        }
        return -1
    }

    fun validateLogin(email: String, password: String): Int {
        val userId = findUserByEmail(email)
        if (userId != -1) {
            val index = userIds.indexOf(userId)
            if (index != -1 && userPasswords[index] == password) {
                currentUserId = userId
                return userId
            }
        }
        return -1
    }

    fun getCurrentUserName(): String {
        val index = userIds.indexOf(currentUserId)
        return if (index != -1) userNames[index] else "User"
    }

    // ==================== CATEGORY OPERATIONS ====================

    fun addCategory(name: String, color: String = "#2dd4bf"): Int {
        val id = nextCategoryId++
        categoryIds.add(id)
        categoryNames.add(name)
        categoryColors.add(color)
        categoryMinBudgets.add(0.0)
        categoryMaxBudgets.add(0.0)
        return id
    }

    fun getCategoryName(categoryId: Int): String {
        val index = categoryIds.indexOf(categoryId)
        return if (index != -1) categoryNames[index] else "Unknown"
    }

    fun getCategoryColor(categoryId: Int): String {
        val index = categoryIds.indexOf(categoryId)
        return if (index != -1) categoryColors[index] else "#2dd4bf"
    }

    // ==================== TRANSACTION OPERATIONS ====================

    fun addTransaction(
        userId: Int,
        amount: Double,
        description: String,
        categoryId: Int,
        date: Long,
        startTime: Long,
        endTime: Long,
        photoPath: String? = null,
        isIncome: Boolean = false
    ): Int {
        val id = nextTransactionId++
        transactionIds.add(id)
        transactionUserIds.add(userId)
        transactionAmounts.add(amount)
        transactionDescriptions.add(description)
        transactionCategoryIds.add(categoryId)
        transactionDates.add(date)
        transactionStartTimes.add(startTime)
        transactionEndTimes.add(endTime)
        transactionPhotoPaths.add(photoPath)
        transactionIsIncome.add(isIncome)
        return id
    }

    fun getTransactionsForPeriod(userId: Int, startDate: Long, endDate: Long): List<Int> {
        val result = ArrayList<Int>()
        for (i in transactionUserIds.indices) {
            if (transactionUserIds[i] == userId &&
                transactionDates[i] in startDate..endDate) {
                result.add(transactionIds[i])
            }
        }
        
        // Requirement: Bubble Sort for sorting format
        bubbleSortByDate(result)
        return result
    }

    fun getTransactionsForUser(userId: Int): List<Int> {
        val result = ArrayList<Int>()
        for (i in transactionUserIds.indices) {
            if (transactionUserIds[i] == userId) {
                result.add(transactionIds[i])
            }
        }
        return result
    }

    fun deleteTransaction(transactionId: Int) {
        val index = transactionIds.indexOf(transactionId)
        if (index != -1) {
            transactionIds.removeAt(index)
            transactionUserIds.removeAt(index)
            transactionAmounts.removeAt(index)
            transactionDescriptions.removeAt(index)
            transactionCategoryIds.removeAt(index)
            transactionDates.removeAt(index)
            transactionStartTimes.removeAt(index)
            transactionEndTimes.removeAt(index)
            transactionPhotoPaths.removeAt(index)
            transactionIsIncome.removeAt(index)
        }
    }

    /**
     * Requirement: Bubble Sort implementation for transactions by date (descending)
     */
    private fun bubbleSortByDate(transactionIdList: ArrayList<Int>) {
        val n = transactionIdList.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                val idx1 = transactionIds.indexOf(transactionIdList[j])
                val idx2 = transactionIds.indexOf(transactionIdList[j + 1])
                
                if (transactionDates[idx1] < transactionDates[idx2]) {
                    // Swap
                    val temp = transactionIdList[j]
                    transactionIdList[j] = transactionIdList[j + 1]
                    transactionIdList[j + 1] = temp
                }
            }
        }
    }

    fun getCategoryTotal(userId: Int, categoryId: Int, startDate: Long, endDate: Long): Double {
        var total = 0.0
        for (i in transactionUserIds.indices) {
            if (transactionUserIds[i] == userId &&
                transactionCategoryIds[i] == categoryId &&
                transactionDates[i] in startDate..endDate &&
                !transactionIsIncome[i]) {
                total += transactionAmounts[i]
            }
        }
        return total
    }

    fun getIncomeTotal(userId: Int, startDate: Long, endDate: Long): Double {
        var total = 0.0
        for (i in transactionUserIds.indices) {
            if (transactionUserIds[i] == userId &&
                transactionDates[i] in startDate..endDate &&
                transactionIsIncome[i]) {
                total += transactionAmounts[i]
            }
        }
        return total
    }

    fun getExpenseTotal(userId: Int, startDate: Long, endDate: Long): Double {
        var total = 0.0
        for (i in transactionUserIds.indices) {
            if (transactionUserIds[i] == userId &&
                transactionDates[i] in startDate..endDate &&
                !transactionIsIncome[i]) {
                total += transactionAmounts[i]
            }
        }
        return total
    }

    // ==================== BUDGET GOAL OPERATIONS ====================

    fun addBudgetGoal(
        userId: Int,
        categoryId: Int,
        minAmount: Double,
        maxAmount: Double,
        monthYear: String
    ): Int {
        val id = nextBudgetGoalId++
        budgetGoalIds.add(id)
        budgetGoalUserIds.add(userId)
        budgetGoalCategoryIds.add(categoryId)
        budgetGoalMinAmounts.add(minAmount)
        budgetGoalMaxAmounts.add(maxAmount)
        budgetGoalMonthYears.add(monthYear)
        return id
    }

    fun getBudgetGoal(userId: Int, categoryId: Int, monthYear: String): BudgetGoal? {
        for (i in budgetGoalUserIds.indices) {
            if (budgetGoalUserIds[i] == userId &&
                budgetGoalCategoryIds[i] == categoryId &&
                budgetGoalMonthYears[i] == monthYear) {
                return BudgetGoal(
                    budgetGoalIds[i],
                    budgetGoalUserIds[i],
                    budgetGoalCategoryIds[i],
                    budgetGoalMinAmounts[i],
                    budgetGoalMaxAmounts[i],
                    budgetGoalMonthYears[i]
                )
            }
        }
        return null
    }

    // ==================== UTILITY ====================

    fun getCurrentMonthYear(): String {
        val sdf = java.text.SimpleDateFormat("MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getMonthStartEnd(monthYear: String): Pair<Long, Long> {
        return try {
            val parts = monthYear.split("-")
            val month = parts[0].toInt() - 1
            val year = parts[1].toInt()
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1, 0, 0, 0)
            val start = calendar.timeInMillis
            calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val end = calendar.timeInMillis
            Pair(start, end)
        } catch (e: Exception) {
            Pair(0L, Long.MAX_VALUE)
        }
    }
}
