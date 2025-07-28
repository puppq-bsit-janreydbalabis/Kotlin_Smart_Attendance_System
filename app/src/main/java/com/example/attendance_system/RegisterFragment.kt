package com.example.attendance_system

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.example.attendance_system.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var selectedRole: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button functionality - go back to MainActivity
        binding.backButton.setOnClickListener {
            // Close this activity and return to MainActivity
            requireActivity().finish()
        }

        // Individual role selection listeners with checkboxes
        binding.studentChip.setOnClickListener {
            binding.studentCheckBox.isChecked = true
            binding.facultyCheckBox.isChecked = false
            selectedRole = "Student"
        }

        binding.facultyChip.setOnClickListener {
            binding.facultyCheckBox.isChecked = true
            binding.studentCheckBox.isChecked = false
            selectedRole = "Faculty"
        }

        // Sign Up button
        binding.signUpButton.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val fullName = binding.fullNameEditText.text.toString().trim()
        val pupId = binding.pupIdEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }

        if (pupId.isEmpty()) {
            Toast.makeText(context, "Please enter your PUP ID", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedRole.isEmpty()) {
            Toast.makeText(context, "Please select a role", Toast.LENGTH_SHORT).show()
            return
        }

        // For demonstration, create email from PUP ID
        val email = "$pupId@pup.edu.ph"

        // Create account with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                // Navigate back to login
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}