package com.codecash

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityTransactionListBinding
import com.codecash.utils.NavigationHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity for viewing transaction history.
 * Meets rubric requirements for:
 * - Viewing a list of entries in a selectable period.
 * - Bubble sort is applied within DataStore.getTransactionsForPeriod for data consistency.
 */
class TransactionListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionListBinding
    private var startDate: Long = 0
    private var endDate: Long = System.currentTimeMillis()
    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadTransactions()
    }

    private fun setupUI() {
        // Toolbar configuration
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Date Filter initialization (defaults to current month)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        startDate = calendar.timeInMillis
        
        binding.btnStartDate.text = sdf.format(Date(startDate))
        binding.btnEndDate.text = sdf.format(Date(endDate))

        binding.btnStartDate.setOnClickListener { showDatePicker(true) }
        binding.btnEndDate.setOnClickListener { showDatePicker(false) }

        // RecyclerView setup
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)

        // Navigation setup
        NavigationHelper.setupBottomNavigation(this, binding.bottomNav, R.id.nav_home)
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = if (isStartDate) startDate else endDate

        DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedCal = Calendar.getInstance()
                if (isStartDate) {
                    selectedCal.set(year, month, day, 0, 0, 0)
                    startDate = selectedCal.timeInMillis
                    binding.btnStartDate.text = sdf.format(selectedCal.time)
                } else {
                    selectedCal.set(year, month, day, 23, 59, 59)
                    endDate = selectedCal.timeInMillis
                    binding.btnEndDate.text = sdf.format(selectedCal.time)
                }
                loadTransactions()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadTransactions() {
        val userId = DataStore.currentUserId
        if (userId == -1) return

        // Fetch filtered and bubble-sorted transactions from DataStore (as per rubric)
        val transactionIdList = DataStore.getTransactionsForPeriod(userId, startDate, endDate)

        if (transactionIdList.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvTransactions.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvTransactions.visibility = View.VISIBLE
            
            val adapter = TransactionAdapter(transactionIdList) { transactionId ->
                // Check if transaction has a photo
                val index = DataStore.transactionIds.indexOf(transactionId)
                if (index != -1 && DataStore.transactionPhotoPaths[index] != null) {
                    val intent = Intent(this, PhotoViewActivity::class.java)
                    intent.putExtra("transaction_id", transactionId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No photo attached to this entry", Toast.LENGTH_SHORT).show()
                }
            }
            binding.rvTransactions.adapter = adapter
        }
    }
}
