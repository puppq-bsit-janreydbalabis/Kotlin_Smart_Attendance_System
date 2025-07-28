package com.example.attendance_system

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.attendance_system.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var isForgotPasswordLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Get user type from NavigationActivity
        val userType = activity?.intent?.getStringExtra("userType") ?: "student"
        
        // Update UI based on user type
        updateUIForUserType(userType)

        // Back button functionality
        binding.backButton.setOnClickListener {
            // Close this activity and return to MainActivity
            requireActivity().finish()
        }

        binding.loginButton.setOnClickListener {
            val userNumber = binding.studentNoEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (userNumber.isNotEmpty() && password.isNotEmpty()) {
                // You can implement different login logic based on userType
                performLogin(userNumber, password, userType)
            } else {
                Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.forgotPasswordTextView.setOnClickListener {
            if (!isForgotPasswordLoading) {
                setForgotPasswordLoadingState(true)
                
                // X app-style brief delay for tactile feedback
                binding.forgotPasswordTextView.postDelayed({
                    // Navigate to reset password screen with smooth transition
                    val bundle = Bundle()
                    bundle.putString("userType", userType)
                    findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment, bundle)
                    
                    // Reset loading state after navigation
                    setForgotPasswordLoadingState(false)
                }, 200) // X app-style 200ms delay for responsive feedback
            }
        }
    }

    private fun setForgotPasswordLoadingState(loading: Boolean) {
        isForgotPasswordLoading = loading
        binding.forgotPasswordTextView.isEnabled = !loading
        
        if (loading) {
            binding.forgotPasswordLoadingBar.visibility = View.VISIBLE
        } else {
            binding.forgotPasswordLoadingBar.visibility = View.GONE
        }
    }

    private fun updateUIForUserType(userType: String) {
        when (userType) {
            "student" -> {
                // Keep default student login
                binding.studentNoEditText.hint = getString(R.string.student_id_hint)
                binding.studentLoginTextView.text = getString(R.string.student_login_title)
            }
            "faculty" -> {
                // Change to professor login
                binding.studentNoEditText.hint = getString(R.string.professor_id_hint)
                // Change title to Faculty Login
                binding.studentLoginTextView.text = getString(R.string.faculty_login_title)
            }
            "admin" -> {
                // Change to admin login
                binding.studentNoEditText.hint = getString(R.string.admin_id_hint)
                // Change title to Admin Login
                binding.studentLoginTextView.text = getString(R.string.admin_login_title)
            }
        }
    }

    private fun performLogin(userNumber: String, password: String, userType: String) {
        // For now, using Firebase Auth for all types
        // You can implement different authentication logic for each user type
        auth.signInWithEmailAndPassword(userNumber, password)
            .addOnSuccessListener {
                val message = when (userType) {
                    "faculty" -> "Professor login successful!"
                    "admin" -> "Admin login successful!"
                    else -> "Student login successful!"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                
                // Navigate to appropriate home screen based on user type
                when (userType) {
                    "faculty" -> findNavController().navigate(R.id.action_loginFragment_to_professorHomeFragment)
                    "admin" -> {
                        // Navigate to admin home when available
                        Toast.makeText(context, "Admin dashboard coming soon", Toast.LENGTH_SHORT).show()
                    }
                    else -> findNavController().navigate(R.id.action_loginFragment_to_studentHomeFragment)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}