package com.codecash

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityBudgetGoalsBinding
import com.codecash.utils.NavigationHelper

/**
 * Activity for managing budget goals.
 * Meets rubric requirements for:
 * - Setting minimum and maximum monthly goals for money spent.
 * - Viewing budget status and progress.
 */
class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetGoalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        updateBudgetData()
    }

    private fun setupUI() {
        // Toolbar
        binding.toolbar.setNavigationOnClickListener { finish() }

        // RecyclerView setup for category budgets
        binding.rvCategoryBudgets.layoutManager = LinearLayoutManager(this)

        // Set Goals Button (Overall)
        binding.btnSetGoals.setOnClickListener {
            showOverallBudgetDialog()
        }

        // Setup Bottom Navigation
        NavigationHelper.setupBottomNavigation(this, binding.bottomNav, R.id.nav_budget)
    }

    private fun updateBudgetData() {
        val userId = DataStore.currentUserId
        if (userId == -1) return

        val currentMonth = DataStore.getCurrentMonthYear()
        val (start, end) = DataStore.getMonthStartEnd(currentMonth)

        // 1. Overall Monthly Budget logic
        // We use category ID 0 to represent the overall budget goal
        val overallGoal = DataStore.getBudgetGoal(userId, 0, currentMonth)
        val totalSpent = DataStore.getExpenseTotal(userId, start, end)
        
        val maxBudget = overallGoal?.maxAmount ?: 20000.0
        
        binding.tvBudgetStatus.text = "Spent: R${String.format("%.2f", totalSpent)} of R${String.format("%.2f", maxBudget)}"
        
        val progress = if (maxBudget > 0) (totalSpent / maxBudget * 100).toInt() else 0
        binding.progressBudget.progress = kotlin.math.min(progress, 100)

        // 2. Load Category budgets into RecyclerView
        loadCategoryBudgets(userId, start, end, currentMonth)
    }

    private fun loadCategoryBudgets(userId: Int, start: Long, end: Long, currentMonth: String) {
        // Use all categories except the 'overall' placeholder (if it existed as a real category, 
        // but here category IDs start from 1 in test data).
        // Let's pass all available category IDs to the adapter.
        val adapter = CategoryBudgetAdapter(
            DataStore.categoryIds, 
            userId, 
            start, 
            end, 
            currentMonth
        ) { catId ->
            // Callback when the user wants to set a specific goal for a category
            showCategoryBudgetDialog(catId)
        }
        binding.rvCategoryBudgets.adapter = adapter
    }

    private fun showOverallBudgetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_budget, null)
        val etMin = dialogView.findViewById<EditText>(R.id.etMinBudget)
        val etMax = dialogView.findViewById<EditText>(R.id.etMaxBudget)
        
        val userId = DataStore.currentUserId
        val currentMonth = DataStore.getCurrentMonthYear()
        val existing = DataStore.getBudgetGoal(userId, 0, currentMonth)
        
        etMin.setText(existing?.minAmount?.toString() ?: "0")
        etMax.setText(existing?.maxAmount?.toString() ?: "20000")
        
        AlertDialog.Builder(this)
            .setTitle("Set Overall Monthly Goal")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val min = etMin.text.toString().toDoubleOrNull() ?: 0.0
                val max = etMax.text.toString().toDoubleOrNull() ?: 20000.0
                
                // Update in DataStore
                saveOrUpdateGoal(userId, 0, min, max, currentMonth)
                
                updateBudgetData()
                Toast.makeText(this, "Monthly goals updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCategoryBudgetDialog(categoryId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_budget, null)
        val etMin = dialogView.findViewById<EditText>(R.id.etMinBudget)
        val etMax = dialogView.findViewById<EditText>(R.id.etMaxBudget)
        
        val userId = DataStore.currentUserId
        val currentMonth = DataStore.getCurrentMonthYear()
        val existing = DataStore.getBudgetGoal(userId, categoryId, currentMonth)
        
        etMin.setText(existing?.minAmount?.toString() ?: "0")
        etMax.setText(existing?.maxAmount?.toString() ?: "0")
        
        AlertDialog.Builder(this)
            .setTitle("Set Category Goal")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val min = etMin.text.toString().toDoubleOrNull() ?: 0.0
                val max = etMax.text.toString().toDoubleOrNull() ?: 0.0
                
                saveOrUpdateGoal(userId, categoryId, min, max, currentMonth)
                
                updateBudgetData()
                Toast.makeText(this, "Category goal updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveOrUpdateGoal(userId: Int, categoryId: Int, min: Double, max: Double, monthYear: String) {
        val index = DataStore.budgetGoalIds.indices.find {
            DataStore.budgetGoalUserIds[it] == userId && 
            DataStore.budgetGoalCategoryIds[it] == categoryId && 
            DataStore.budgetGoalMonthYears[it] == monthYear 
        }
        
        if (index != null) {
            DataStore.budgetGoalMinAmounts[index] = min
            DataStore.budgetGoalMaxAmounts[index] = max
        } else {
            DataStore.addBudgetGoal(userId, categoryId, min, max, monthYear)
        }
    }
}
