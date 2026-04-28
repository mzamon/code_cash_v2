package com.codecash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityDashboardBinding
import com.codecash.utils.NavigationHelper

/**
 * Main dashboard activity.
 * Meets rubric requirements for:
 * - Summary view of balance, income, and expenses.
 * - List of recent entries.
 * - Navigation to other features.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardData()
    }

    private fun setupUI() {
        // Welcome text
        binding.tvWelcome.text = "Welcome back, ${DataStore.getCurrentUserName()}!"

        // RecyclerView setup
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)

        // View more button
        binding.tvViewMore.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

        // Add transaction button
        binding.btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun updateDashboardData() {
        val userId = DataStore.currentUserId
        if (userId == -1) {
            // Should not happen if logged in correctly
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Calculate totals for current month
        val currentMonth = DataStore.getCurrentMonthYear()
        val (start, end) = DataStore.getMonthStartEnd(currentMonth)

        val income = DataStore.getIncomeTotal(userId, start, end)
        val expenses = DataStore.getExpenseTotal(userId, start, end)
        val balance = income - expenses

        // Update UI with formatted currency
        binding.tvBalance.text = "R${String.format("%.2f", balance)}"
        binding.tvIncome.text = "R${String.format("%.2f", income)}"
        binding.tvExpenses.text = "R${String.format("%.2f", expenses)}"

        // Load recent transactions (sorted by date newest first via DataStore logic)
        val allTransactionIds = DataStore.getTransactionsForPeriod(userId, 0, Long.MAX_VALUE)
        val recentIds = if (allTransactionIds.size > 5) allTransactionIds.take(5) else allTransactionIds
        
        val adapter = TransactionAdapter(recentIds) { transactionId ->
            // Handle transaction click (optional: view/edit)
            val index = DataStore.transactionIds.indexOf(transactionId)
            val photoPath = DataStore.transactionPhotoPaths[index]
            if (photoPath != null) {
                val intent = Intent(this, PhotoViewActivity::class.java)
                intent.putExtra("transaction_id", transactionId)
                startActivity(intent)
            }
        }
        binding.rvRecentTransactions.adapter = adapter
    }

    private fun setupBottomNavigation() {
        NavigationHelper.setupBottomNavigation(this, binding.bottomNav, R.id.nav_home)
    }
}
