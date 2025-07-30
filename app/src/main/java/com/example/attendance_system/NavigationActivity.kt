package com.example.attendance_system

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class NavigationActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Set up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Handle back button using modern OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When back is pressed, finish this activity to return to MainActivity
                if (navController.currentDestination?.id == R.id.loginFragment ||
                    navController.currentDestination?.id == R.id.registerFragment) {
                    finish() // This will return to MainActivity
                } else if (navController.currentDestination?.id == R.id.studentHomeFragment ||
                           navController.currentDestination?.id == R.id.professorHomeFragment) {
                    // Prevent back navigation from home fragments to login
                    // The fragments will handle their own back button behavior
                    return
                } else {
                    // If we're in other fragments, use default back behavior
                    if (!navController.navigateUp()) {
                        finish()
                    }
                }
            }
        })

        // Navigate based on intent extras
        val destination = intent.getStringExtra("destination")
        val userType = intent.getStringExtra("userType")

        when (destination) {
            "login" -> {
                navController.navigate(R.id.loginFragment)
            }
            "register" -> {
                navController.navigate(R.id.registerFragment)
            }
            "home" -> {
                // Navigate directly to appropriate home screen based on user type
                when (userType) {
                    "faculty" -> navController.navigate(R.id.professorHomeFragment)
                    "admin" -> {
                        // For now, show admin message and go to professor home
                        Toast.makeText(this, "Admin dashboard coming soon", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.professorHomeFragment)
                    }
                    else -> navController.navigate(R.id.studentHomeFragment)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
    }
} 