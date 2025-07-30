package com.example.attendance_system

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.attendance_system.databinding.FragmentProfessorHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.os.Handler
import android.os.Looper

class ProfessorHomeFragment : Fragment() {
    private var _binding: FragmentProfessorHomeBinding? = null
    private val binding get() = _binding!!

    // Resource management
    private var loadingDialog: AlertDialog? = null
    private var signOutDialog: AlertDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var signOutRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Prevent back button from returning to login screen
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show a toast message instead of going back
                Toast.makeText(context, "Please use the sign out button to logout", Toast.LENGTH_SHORT).show()
            }
        })
        
        setupClickListeners()
        setupNavigationDrawer()
        setupNavigationHeader()
        setCurrentNavigationItem()
    }

    private fun setCurrentNavigationItem() {
        // Set the Home menu item as checked to indicate current location
        binding.navigationView.menu.findItem(R.id.nav_home)?.isChecked = true
    }

    private fun setupNavigationHeader() {
        // Add a small delay to ensure NavigationView is ready
        handler.postDelayed({
            try {
                // Create header with correct text
                val headerView = LayoutInflater.from(context).inflate(R.layout.nav_header, binding.navigationView, false)
                
                // Find the TextViews in the header
                val userTypeTextView = headerView.findViewById<TextView>(R.id.userType)
                val userEmailTextView = headerView.findViewById<TextView>(R.id.userEmail)
                val profileImageView = headerView.findViewById<ImageView>(R.id.profileImage)
                val coverPhotoContainer = headerView.findViewById<View>(R.id.coverPhotoContainer)
                
                if (userTypeTextView != null && userEmailTextView != null) {
                    // Set the user information for professor
                    userTypeTextView.setText("Professor")
                    userEmailTextView.setText("professor@example.edu")
                    
                    // Set up profile picture
                    if (profileImageView != null) {
                        profileImageView.setImageResource(R.drawable.ic_profile)
                        profileImageView.setColorFilter(resources.getColor(R.color.white, null))
                        
                        // Add click listener for profile picture
                        profileImageView.setOnClickListener {
                            Toast.makeText(context, "Profile picture options", Toast.LENGTH_SHORT).show()
                            // TODO: Implement profile picture selection functionality
                        }
                    }
                    
                    // Set up cover photo area
                    if (coverPhotoContainer != null) {
                        // Add click listener for cover photo container
                        coverPhotoContainer.setOnClickListener {
                            Toast.makeText(context, "Cover photo options", Toast.LENGTH_SHORT).show()
                            // TODO: Implement cover photo selection functionality
                        }
                    }
                    
                    // Set up dropdown menu icon
                    val menuIconView = headerView.findViewById<ImageView>(R.id.menuIcon)
                    if (menuIconView != null) {
                        menuIconView.setOnClickListener {
                            Toast.makeText(context, "Account options", Toast.LENGTH_SHORT).show()
                            // TODO: Implement account options menu
                        }
                    }
                    
                    // Add the header to the NavigationView
                    binding.navigationView.addHeaderView(headerView)
                    
                    // Force layout refresh
                    binding.navigationView.requestLayout()
                    
                } else {
                    // Handle case where TextViews are not found
                }
            } catch (e: Exception) {
                // Handle any exceptions during header setup
            }
        }, 100) // 100ms delay
    }

    private fun setupNavigationDrawer() {
        // Setup navigation drawer
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Clear all checked states first
            binding.navigationView.menu.findItem(R.id.nav_home)?.isChecked = false
            binding.navigationView.menu.findItem(R.id.nav_calendar)?.isChecked = false
            binding.navigationView.menu.findItem(R.id.nav_todo)?.isChecked = false
            binding.navigationView.menu.findItem(R.id.nav_settings)?.isChecked = false
            binding.navigationView.menu.findItem(R.id.nav_about)?.isChecked = false
            
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Set Home as checked since we're already on Home
                    menuItem.isChecked = true
                    Toast.makeText(context, "Home selected", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_calendar -> {
                    menuItem.isChecked = true
                    Toast.makeText(context, "Calendar selected", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_todo -> {
                    menuItem.isChecked = true
                    Toast.makeText(context, "To do selected", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings -> {
                    menuItem.isChecked = true
                    Toast.makeText(context, "Settings selected", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_about -> {
                    menuItem.isChecked = true
                    Toast.makeText(context, "About selected", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_sign_out -> {
                    // Don't set checked state for sign out
                    showSignOutDialog()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        // Menu button click - open navigation drawer
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        // Add button click
        binding.addButton.setOnClickListener {
            showAddOptionsDialog()
        }

        // More options button click
        binding.moreButton.setOnClickListener {
            Toast.makeText(context, "More options", Toast.LENGTH_SHORT).show()
        }

        // TODO: Add dynamic course card click listeners when courses are created
        // Course cards will be added dynamically to courseCardsContainer
    }

    private fun showSignOutDialog() {
        signOutDialog?.dismiss() // Dismiss any existing dialog
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign Out") { _, _ ->
                performSignOut()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
        
        // Apply custom button styles after dialog creation
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                resources.getColor(R.color.dialog_danger_button, null)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                resources.getColor(R.color.dialog_negative_button, null)
            )
        }
        
        dialog.show()
        signOutDialog = dialog // Assign the dialog to the class property
    }

    private fun performSignOut() {
        loadingDialog?.dismiss() // Dismiss any existing loading dialog
        loadingDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(R.layout.loading_dialog)
            .setCancelable(false)
            .create()
        loadingDialog?.show()

        // Set the correct text for sign out
        val loadingTitle = loadingDialog?.findViewById<TextView>(R.id.loadingTitle)
        val loadingSubtitle = loadingDialog?.findViewById<TextView>(R.id.loadingSubtitle)
        loadingTitle?.text = "Signing out..."
        loadingSubtitle?.text = "Please wait while we sign you out"

        // Simulate sign out process with delay
        signOutRunnable = Runnable {
            try {
                // Clear user session data
                val sessionManager = SessionManager(requireContext())
                sessionManager.clearSession()
                
                // Close loading dialog
                loadingDialog?.dismiss()
                loadingDialog = null // Clear the reference
                
                // Navigate back to login screen
                navigateToLogin()
                
            } catch (e: Exception) {
                loadingDialog?.dismiss()
                loadingDialog = null // Clear the reference
                Toast.makeText(context, "Error signing out: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        handler.postDelayed(signOutRunnable!!, 2000) // 2 second delay for sign out process
    }

    private fun navigateToLogin() {
        try {
            // Finish the current activity and start MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            
            // Finish the current activity
            requireActivity().finish()
            
            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error navigating to login: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddOptionsDialog() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.add_options_bottom_sheet, null)
        
        // Set up click listeners for the add options
        view.findViewById<View>(R.id.createClassOption).setOnClickListener {
            bottomSheet.dismiss()
            Toast.makeText(context, "Create class functionality coming soon", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<View>(R.id.scanQRCodeOption).setOnClickListener {
            bottomSheet.dismiss()
            Toast.makeText(context, "QR code scanner coming soon", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<View>(R.id.enterClassCodeOption).setOnClickListener {
            bottomSheet.dismiss()
            Toast.makeText(context, "Enter class code functionality coming soon", Toast.LENGTH_SHORT).show()
        }
        

        
        // Set the content view and show the bottom sheet
        bottomSheet.setContentView(view)
        
        // Set the bottom sheet background to transparent
        bottomSheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        bottomSheet.show()
        
        // Configure bottom sheet behavior with rounded corners
        configureBottomSheetBehavior(bottomSheet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Cancel any pending runnables
        handler.removeCallbacksAndMessages(null)
        loadingDialog?.dismiss()
        signOutDialog?.dismiss()
    }

    private fun configureBottomSheetBehavior(bottomSheet: BottomSheetDialog) {
        bottomSheet.behavior.apply {
            isDraggable = false
            isHideable = true
            expandedOffset = 0
            isFitToContents = true
            state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            saveFlags = com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_ALL
        }
        
        // Set the shape appearance on the dialog's window
        bottomSheet.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}