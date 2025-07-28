package com.example.attendance_system

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.attendance_system.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var userType: String = "student"
    private var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Get user type from arguments
        userType = arguments?.getString("userType") ?: "student"
        
        // Update UI based on user type
        updateUIForUserType(userType)

        setupClickListeners()
        
        // Animate form fields with staggered entrance
        animateFormFields()
    }

    private fun updateUIForUserType(userType: String) {
        when (userType) {
            "student" -> {
                binding.pupIdEditText.hint = "Student Number"
            }
            "faculty" -> {
                binding.pupIdEditText.hint = "Professor ID"
            }
            "admin" -> {
                binding.pupIdEditText.hint = "Admin ID"
            }
        }
    }

    private fun setupClickListeners() {
        // Back button functionality - go back to previous screen
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Reset Password button
        binding.resetPasswordButton.setOnClickListener {
            if (!isLoading) {
                performPasswordReset()
            }
        }
    }

    private fun performPasswordReset() {
        val pupId = binding.pupIdEditText.text.toString().trim()
        val newPassword = binding.newPasswordEditText.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPasswordEditText.text.toString().trim()

        // Validation
        if (pupId.isEmpty()) {
            val hintText = when (userType) {
                "student" -> "Student Number"
                "faculty" -> "Professor ID"
                "admin" -> "Admin ID"
                else -> "PUP ID"
            }
            Toast.makeText(context, "Please enter your $hintText", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(context, "Please enter a new password", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        setLoadingState(true)

        // For demonstration, create email from PUP ID
        val email = "$pupId@pup.edu.ph"

        // Reset password with Firebase
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                setLoadingState(false)
                Toast.makeText(context, "Password reset email sent successfully!", Toast.LENGTH_SHORT).show()
                // Navigate back to login
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                setLoadingState(false)
                Toast.makeText(context, "Password reset failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoadingState(loading: Boolean) {
        isLoading = loading
        binding.resetPasswordButton.isEnabled = !loading
        binding.pupIdEditText.isEnabled = !loading
        binding.newPasswordEditText.isEnabled = !loading
        binding.confirmNewPasswordEditText.isEnabled = !loading
        binding.backButton.isEnabled = !loading
        
        if (loading) {
            binding.resetPasswordButton.text = "RESETTING..."
            binding.loadingProgressBar.visibility = View.VISIBLE
            binding.loadingProgressBar.alpha = 0f
            binding.loadingProgressBar.animate().alpha(1f).setDuration(250).start()
        } else {
            binding.resetPasswordButton.text = "RESET PASSWORD"
            binding.loadingProgressBar.animate().alpha(0f).setDuration(250).withEndAction {
                binding.loadingProgressBar.visibility = View.GONE
            }.start()
        }
    }

    private fun animateFormFields() {
        // Start with fields invisible
        binding.pupIdEditText.alpha = 0f
        binding.newPasswordEditText.alpha = 0f
        binding.confirmNewPasswordEditText.alpha = 0f
        binding.resetPasswordButton.alpha = 0f
        
        // Animate fields in with staggered timing
        binding.pupIdEditText.animate().alpha(1f).setDuration(300).setStartDelay(100).start()
        binding.newPasswordEditText.animate().alpha(1f).setDuration(300).setStartDelay(200).start()
        binding.confirmNewPasswordEditText.animate().alpha(1f).setDuration(300).setStartDelay(300).start()
        binding.resetPasswordButton.animate().alpha(1f).setDuration(300).setStartDelay(400).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 