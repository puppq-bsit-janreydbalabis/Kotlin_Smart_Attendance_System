package com.example.attendance_system

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ✅ ADD THIS CLASS DEFINITION BACK
class MainActivity : AppCompatActivity() {

    // Your onCreate function goes inside the class
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        // Launch a coroutine that won't block the UI thread
        lifecycleScope.launch {
            // Run the slow function on a background thread (Dispatchers.IO)
            val userData = withContext(Dispatchers.IO) {
                // loadUserDataFromDatabase() // This runs in the background
            }
            // The coroutine comes back to the main thread automatically
            // to safely update the UI
            // setupUIWithData(userData)
        }
    }

    // You can add other functions for your activity here

}