package com.codecash

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecash.data.DataStore
import com.codecash.databinding.ItemTransactionBinding

/**
 * Adapter for displaying transactions in a RecyclerView.
 * Uses DataStore's parallel arrays for data retrieval.
 */
class TransactionAdapter(
    private val transactionIdList: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transactionId = transactionIdList[position]
        val index = DataStore.transactionIds.indexOf(transactionId)

        if (index != -1) {
            val amount = DataStore.transactionAmounts[index]
            val description = DataStore.transactionDescriptions[index]
            val categoryId = DataStore.transactionCategoryIds[index]
            val dateMillis = DataStore.transactionDates[index]
            val photoPath = DataStore.transactionPhotoPaths[index]
            val isIncome = DataStore.transactionIsIncome[index]

            // Bind values to UI
            holder.binding.tvDescription.text = description
            holder.binding.tvCategory.text = DataStore.getCategoryName(categoryId)
            
            // Format Date
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            holder.binding.tvDate.text = sdf.format(java.util.Date(dateMillis))

            // Format Amount
            val formattedAmount = if (isIncome) "+R${String.format("%.2f", amount)}" else "-R${String.format("%.2f", amount)}"
            holder.binding.tvAmount.text = formattedAmount
            
            // Set Color based on Income/Expense
            val colorRes = if (isIncome) "#22c55e" else "#ef4444"
            holder.binding.tvAmount.setTextColor(Color.parseColor(colorRes))
            
            // Set Category Icon Background Color
            val categoryColor = DataStore.getCategoryColor(categoryId)
            holder.binding.ivCategoryIcon.background.setColorFilter(Color.parseColor(categoryColor), PorterDuff.Mode.SRC_IN)

            // Show Photo Indicator if photo exists
            holder.binding.ivPhotoIndicator.visibility = if (photoPath != null) View.VISIBLE else View.GONE

            // Handle Item Click
            holder.itemView.setOnClickListener {
                onItemClick(transactionId)
            }
        }
    }

    override fun getItemCount(): Int = transactionIdList.size
}
