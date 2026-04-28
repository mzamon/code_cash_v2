package com.codecash

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codecash.data.DataStore
import com.codecash.databinding.ActivitySettingsBinding
import com.codecash.utils.NavigationHelper

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        loadUserInfo()
        setupSwitches()
        setupButtons()
    }
    
    private fun setupBottomNavigation() {
        NavigationHelper.setupBottomNavigation(this, binding.bottomNav, R.id.nav_settings)
    }
    
    private fun loadUserInfo() {
        val userId = DataStore.currentUserId
        if (userId != -1) {
            val idx = DataStore.userIds.indexOf(userId)
            if (idx != -1) {
                binding.tvUserName.text = DataStore.userNames[idx]
                binding.tvUserEmail.text = DataStore.userEmails[idx]
            }
        }
    }
    
    private fun setupSwitches() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Theme switching would be implemented here
            Toast.makeText(this, if (isChecked) "Dark mode enabled" else "Light mode enabled", Toast.LENGTH_SHORT).show()
        }
        
        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, if (isChecked) "Biometric enabled" else "Biometric disabled", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupButtons() {
        binding.btnAddBank.setOnClickListener {
            Toast.makeText(this, "Bank integration coming in v3.0", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnDeleteAccount.setOnClickListener {
            confirmDeleteAccount()
        }
        
        binding.btnLogout.setOnClickListener {
            DataStore.currentUserId = -1
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
    
    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This will permanently delete all your data. Continue?")
            .setPositiveButton("Delete") { _, _ ->
                val userId = DataStore.currentUserId
                if (userId != -1) {
                    // Remove user data
                    val idx = DataStore.userIds.indexOf(userId)
                    if (idx != -1) {
                        DataStore.userIds.removeAt(idx)
                        DataStore.userNames.removeAt(idx)
                        DataStore.userEmails.removeAt(idx)
                        DataStore.userPasswords.removeAt(idx)
                    }
                    
                    // Remove associated transactions
                    val transToRemove = DataStore.getTransactionsForUser(userId)
                    for (transId in transToRemove) {
                        DataStore.deleteTransaction(transId)
                    }
                    
                    DataStore.currentUserId = -1
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}