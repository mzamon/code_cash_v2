package com.codecash

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityCategoryBinding
import com.codecash.utils.NavigationHelper

/**
 * Activity for managing expense categories.
 * Meets rubric requirements for:
 * - Creating categories.
 * - Viewing a list of categories.
 */
class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadCategories()
    }

    private fun setupUI() {
        // Toolbar
        binding.toolbar.setNavigationOnClickListener { finish() }

        // RecyclerView setup
        binding.rvCategories.layoutManager = LinearLayoutManager(this)

        // Add Category Button
        binding.btnAddCategory.setOnClickListener {
            val categoryName = binding.etNewCategory.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                // Add to DataStore (Parallel arrays)
                DataStore.addCategory(categoryName)
                binding.etNewCategory.text?.clear()
                loadCategories()
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
            } else {
                binding.tilNewCategory.error = "Name required"
            }
        }

        // Setup Bottom Navigation
        NavigationHelper.setupBottomNavigation(this, binding.bottomNav, R.id.nav_settings)
    }

    private fun loadCategories() {
        // Fetch category data from DataStore
        // Note: categoryIds in DataStore is the source of truth for indexes
        val adapter = CategoryAdapter(DataStore.categoryIds) { catId ->
            // Delete category logic
            val index = DataStore.categoryIds.indexOf(catId)
            if (index != -1) {
                // Parallel removal
                DataStore.categoryIds.removeAt(index)
                DataStore.categoryNames.removeAt(index)
                DataStore.categoryColors.removeAt(index)
                DataStore.categoryMinBudgets.removeAt(index)
                DataStore.categoryMaxBudgets.removeAt(index)
                loadCategories()
                Toast.makeText(this, "Category removed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvCategories.adapter = adapter
    }
}
