package com.codecash

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecash.data.DataStore
import com.codecash.databinding.ItemCategoryBinding

/**
 * Adapter for displaying categories in a list.
 */
class CategoryAdapter(
    private val categoryIdList: List<Int>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val catId = categoryIdList[position]
        val index = DataStore.categoryIds.indexOf(catId)

        if (index != -1) {
            holder.binding.tvCategoryName.text = DataStore.categoryNames[index]
            
            val min = DataStore.categoryMinBudgets[index]
            val max = DataStore.categoryMaxBudgets[index]
            holder.binding.tvBudgetRange.text = "Goal: R${String.format("%.0f", min)} - R${String.format("%.0f", max)}"
            
            holder.binding.vColor.setBackgroundColor(Color.parseColor(DataStore.categoryColors[index]))

            holder.binding.btnDelete.setOnClickListener {
                onDeleteClick(catId)
            }
        }
    }

    override fun getItemCount(): Int = categoryIdList.size
}
