package com.codecash

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecash.data.DataStore
import com.codecash.databinding.ItemCategoryBinding

/**
 * Adapter for displaying category budget goals and current progress.
 * Reuses ItemCategoryBinding but adapts it for budget visualization.
 */
class CategoryBudgetAdapter(
    private val categoryIdList: List<Int>,
    private val userId: Int,
    private val start: Long,
    private val end: Long,
    private val monthYear: String,
    private val onSetBudgetClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryBudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val catId = categoryIdList[position]
        val index = DataStore.categoryIds.indexOf(catId)

        if (index != -1) {
            val catName = DataStore.categoryNames[index]
            val catColor = DataStore.categoryColors[index]
            
            val spent = DataStore.getCategoryTotal(userId, catId, start, end)
            val goal = DataStore.getBudgetGoal(userId, catId, monthYear)
            
            // If no goal set for this month, use category default max
            val maxBudget = goal?.maxAmount ?: DataStore.categoryMaxBudgets[index]

            holder.binding.tvCategoryName.text = catName
            holder.binding.vColor.background.setColorFilter(Color.parseColor(catColor), PorterDuff.Mode.SRC_IN)
            
            // Use tvBudgetRange to show Spent vs Goal
            holder.binding.tvBudgetRange.text = "Spent: R${String.format("%.2f", spent)} / Goal: R${String.format("%.2f", maxBudget)}"
            
            // Hide delete button and show "Edit Goal" behavior if clicked
            holder.binding.btnDelete.setImageResource(R.drawable.ic_settings)
            holder.binding.btnDelete.setColorFilter(Color.parseColor("#2dd4bf"))
            holder.binding.btnDelete.setOnClickListener {
                onSetBudgetClick(catId)
            }
            
            // Optional: Add visual indicator if over budget
            if (maxBudget > 0 && spent > maxBudget) {
                holder.binding.tvBudgetRange.setTextColor(Color.parseColor("#ef4444"))
            } else {
                holder.binding.tvBudgetRange.setTextColor(Color.parseColor("#9ca3af")) // gray_light
            }
        }
    }

    override fun getItemCount(): Int = categoryIdList.size
}
