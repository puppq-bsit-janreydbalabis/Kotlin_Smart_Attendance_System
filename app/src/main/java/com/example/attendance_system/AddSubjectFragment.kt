package com.example.attendance_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.attendance_system.databinding.FragmentAddSubjectBinding // Import your generated binding class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddSubjectFragment : Fragment() {

    // Declare binding and Firebase variables
    private var _binding: FragmentAddSubjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // *** ALL YOUR LOGIC GOES HERE ***
        binding.createSubjectButton.setOnClickListener { // Assumes your button ID is 'createSubjectButton'
            createSubject()
        }
    }

    private fun createSubject() {
        val subjectName = binding.subjectNameEditText.text.toString()
        val section = binding.sectionEditText.text.toString()
        val schedule = binding.scheduleEditText.text.toString()
        val enrollmentCode = generateRandomCode()
        val currentUser = auth.currentUser

        if(currentUser == null) {
            // Handle case where user is not logged in
            return
        }

        val subject = hashMapOf(
            "subjectName" to subjectName,
            "section" to section,
            "schedule" to schedule,
            "professorId" to currentUser.uid,
            "enrollmentCode" to enrollmentCode
        )

        firestore.collection("subjects").add(subject)
            .addOnSuccessListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Subject Created!")
                    .setMessage("Share this code with your students: $enrollmentCode")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().popBackStack() // Go back to the previous screen
                    }
                    .show()
            }
    }

    private fun generateRandomCode(length: Int = 6): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}