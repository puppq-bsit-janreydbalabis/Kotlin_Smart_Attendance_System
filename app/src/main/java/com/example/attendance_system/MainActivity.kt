package com.example.attendance_system

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.attendance_system.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is already logged in
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            // User is already logged in, navigate directly to appropriate home screen
            val userType = sessionManager.getUserType() ?: "student"
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("destination", "home")
            intent.putExtra("userType", userType)
            intent.putExtra("userId", sessionManager.getUserId())
            intent.putExtra("userEmail", sessionManager.getUserEmail())
            startActivity(intent)
            finish() // Close MainActivity so user can't go back to it
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Set click listener for student button
        binding.studentButton.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("destination", "login")
            intent.putExtra("userType", "student")
            startActivity(intent)
        }

        // Set click listener for faculty button
        binding.facultyButton.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("destination", "login")
            intent.putExtra("userType", "faculty")
            startActivity(intent)
        }

        // Set click listener for admin button
        binding.adminButton.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("destination", "login")
            intent.putExtra("userType", "admin")
            startActivity(intent)
        }

        // Set click listener for sign up text
        binding.signUpText.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra("destination", "register")
            intent.putExtra("userType", "student")
            startActivity(intent)
        }
    }
}