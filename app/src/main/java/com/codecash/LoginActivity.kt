package com.codecash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Contact support to reset password", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        // Validation
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email required"
            return
        }
        binding.tilEmail.error = null
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password required"
            return
        }
        binding.tilPassword.error = null
        
        // Attempt login
        val userId = DataStore.validateLogin(email, password)
        
        if (userId != -1) {
            // Success
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            // Generic error for security
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }
}