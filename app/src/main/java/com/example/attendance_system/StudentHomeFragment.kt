package com.example.attendance_system

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.attendance_system.databinding.FragmentStudentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog


class StudentHomeFragment : Fragment() {
    private var _binding: FragmentStudentHomeBinding? = null
    private val binding get() = _binding!!

    // Image selection launchers
    private val profileImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                setProfileImage(uri)
            }
        }
    }

    private val coverImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                setCoverImage(uri)
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.extras?.get("data")?.let { bitmap ->
                setImageFromCamera(bitmap as Bitmap)
            }
        }
    }

    private var currentImageType: String = "profile" // "profile" or "cover"
    
    // Resource management
    private var loadingDialog: AlertDialog? = null
    private var signOutDialog: AlertDialog? = null
    private var imagePickerDialog: BottomSheetDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var signOutRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d("StudentHomeFragment", "onViewCreated called")
        Log.d("StudentHomeFragment", "NavigationView is null: ${binding.navigationView == null}")
        
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
        Log.d("StudentHomeFragment", "Setting up navigation header...")
        
        // Add a small delay to ensure NavigationView is ready
        handler.postDelayed({
            try {
                // Create header with correct text
                val headerView = LayoutInflater.from(context).inflate(R.layout.nav_header, binding.navigationView, false)
                
                // Find the TextViews in the header
                val userTypeTextView = headerView.findViewById<TextView>(R.id.userType)
                val userEmailTextView = headerView.findViewById<TextView>(R.id.userEmail)
                val profileImageView = headerView.findViewById<ImageView>(R.id.profileImage)
                val coverPhotoView = headerView.findViewById<ImageView>(R.id.coverPhoto)
                val coverPhotoContainer = headerView.findViewById<View>(R.id.coverPhotoContainer)
                val addCoverPhotoIndicator = headerView.findViewById<View>(R.id.addCoverPhotoIndicator)
                
                if (userTypeTextView != null && userEmailTextView != null) {
                    // Set the user information
                    userTypeTextView.setText("Student")
                    userEmailTextView.setText("student@example.edu")
                    
                    Log.d("StudentHomeFragment", "Header text set successfully")
                    Log.d("StudentHomeFragment", "UserType: ${userTypeTextView.text}")
                    Log.d("StudentHomeFragment", "UserEmail: ${userEmailTextView.text}")
                    
                    // Set up profile picture (you can replace this with actual user profile image)
                    if (profileImageView != null) {
                        // For now, using the default profile icon
                        // TODO: Replace with actual user profile image from storage or network
                        profileImageView.setImageResource(R.drawable.ic_profile)
                        profileImageView.setColorFilter(resources.getColor(R.color.white, null))
                        
                        // Add click listener for profile picture
                        profileImageView.setOnClickListener {
                            currentImageType = "profile"
                            showImagePickerDialog()
                        }
                    }
                    
                    // Set up cover photo area
                    if (coverPhotoContainer != null) {
                        // Add click listener for cover photo container
                        coverPhotoContainer.setOnClickListener {
                            currentImageType = "cover"
                            showImagePickerDialog()
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
                    
                    Log.d("StudentHomeFragment", "Header added to NavigationView")
                    
                    // Show a toast to confirm the setup
                    Toast.makeText(context, "Header added: ${userEmailTextView.text}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("StudentHomeFragment", "TextViews not found in header layout")
                }
            } catch (e: Exception) {
                Log.e("StudentHomeFragment", "Error adding header: ${e.message}")
                e.printStackTrace()
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

        // TODO: Add dynamic course card click listeners when courses are joined
        // Course cards will be added dynamically to courseCardsContainer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Dismiss dialogs on fragment destruction
        loadingDialog?.dismiss()
        signOutDialog?.dismiss()
        imagePickerDialog?.dismiss()
        // Cancel any pending runnables
        handler.removeCallbacksAndMessages(null)
    }

    private fun showAddOptionsDialog() {
        // Dismiss any existing dialog
        imagePickerDialog?.dismiss()
        
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.add_options_bottom_sheet, null)
        
        // Set up click listeners for the add options
        view.findViewById<View>(R.id.scanQRCodeOption).setOnClickListener {
            bottomSheet.dismiss()
            Toast.makeText(context, "QR code scanner coming soon", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<View>(R.id.enterClassCodeOption).setOnClickListener {
            bottomSheet.dismiss()
            Toast.makeText(context, "Enter class code functionality coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Hide the create class option for students
        view.findViewById<View>(R.id.createClassOption).visibility = View.GONE
        
        // Set the content view and show the bottom sheet
        bottomSheet.setContentView(view)
        
        // Set the bottom sheet background to transparent
        bottomSheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        bottomSheet.show()
        
        // Add iOS-style smooth animation and behavior - Fixed position
        bottomSheet.behavior.apply {
            isDraggable = false
            isHideable = true
            expandedOffset = 0
            isFitToContents = true
            state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            saveFlags = com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_ALL
        }
        
        // The rounded corners are now handled by the MaterialCardView in the layout
        // and the TransparentBottomSheetDialog style
        
        imagePickerDialog = bottomSheet // Assign to class property
    }

    private fun showImagePickerDialog() {
        // Dismiss any existing dialog
        imagePickerDialog?.dismiss()
        
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.image_picker_bottom_sheet, null)
        
        // Set up click listeners for the modern card-based layout
        view.findViewById<View>(R.id.cameraOption).setOnClickListener {
            bottomSheet.dismiss()
            openCamera()
        }
        
        view.findViewById<View>(R.id.galleryOption).setOnClickListener {
            bottomSheet.dismiss()
            openGallery()
        }
        
        // Set the content view and show the bottom sheet
        bottomSheet.setContentView(view)
        
        // Set the bottom sheet background to transparent
        bottomSheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        bottomSheet.show()
        
        // Add iOS-style smooth animation and behavior - Fixed position
        bottomSheet.behavior.apply {
            isDraggable = false
            isHideable = true
            expandedOffset = 0
            isFitToContents = true
            state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            saveFlags = com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_ALL
        }
        
        // The rounded corners are now handled by the MaterialCardView in the layout
        // and the TransparentBottomSheetDialog style
        
        imagePickerDialog = bottomSheet // Assign to class property
    }



    private fun openCamera() {
        if (checkCameraPermission()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } else {
            requestCameraPermission()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (currentImageType == "profile") {
            profileImageLauncher.launch(intent)
        } else {
            coverImageLauncher.launch(intent)
        }
    }

    private fun setProfileImage(uri: Uri) {
        try {
            val headerView = binding.navigationView.getHeaderView(0)
            val profileImageView = headerView.findViewById<ImageView>(R.id.profileImage)
            if (profileImageView != null && context != null) {
                profileImageView.setImageURI(uri)
                profileImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("StudentHomeFragment", "Error setting profile image: ${e.message}")
            Toast.makeText(context, "Error setting profile image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCoverImage(uri: Uri) {
        try {
            val headerView = binding.navigationView.getHeaderView(0)
            val coverPhotoView = headerView.findViewById<ImageView>(R.id.coverPhoto)
            val addCoverPhotoIndicator = headerView.findViewById<View>(R.id.addCoverPhotoIndicator)
            
            if (coverPhotoView != null && context != null) {
                coverPhotoView.setImageURI(uri)
                coverPhotoView.scaleType = ImageView.ScaleType.CENTER_CROP
                
                // Hide the indicator when cover photo is set
                addCoverPhotoIndicator?.visibility = View.GONE
                
                Toast.makeText(context, "Cover photo updated!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("StudentHomeFragment", "Error setting cover image: ${e.message}")
            Toast.makeText(context, "Error setting cover image", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setImageFromCamera(bitmap: Bitmap) {
        try {
            val headerView = binding.navigationView.getHeaderView(0)
            if (currentImageType == "profile") {
                val profileImageView = headerView.findViewById<ImageView>(R.id.profileImage)
                if (profileImageView != null && context != null) {
                    profileImageView.setImageBitmap(bitmap)
                    profileImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val coverPhotoView = headerView.findViewById<ImageView>(R.id.coverPhoto)
                val addCoverPhotoIndicator = headerView.findViewById<View>(R.id.addCoverPhotoIndicator)
                if (coverPhotoView != null && context != null) {
                    coverPhotoView.setImageBitmap(bitmap)
                    coverPhotoView.scaleType = ImageView.ScaleType.CENTER_CROP
                    addCoverPhotoIndicator?.visibility = View.GONE
                    Toast.makeText(context, "Cover photo updated!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("StudentHomeFragment", "Error setting image from camera: ${e.message}")
            Toast.makeText(context, "Error setting image from camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    private fun showSignOutDialog() {
        // Dismiss any existing dialog
        signOutDialog?.dismiss()
        
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
                ContextCompat.getColor(requireContext(), R.color.dialog_danger_button)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.dialog_negative_button)
            )
        }
        
        dialog.show()
        signOutDialog = dialog // Assign to class property
    }

    private fun performSignOut() {
        // Dismiss any existing loading dialog
        loadingDialog?.dismiss()
        
        // Show loading dialog
        val loadingDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(R.layout.loading_dialog)
            .setCancelable(false)
            .create()
        loadingDialog.show()
        this.loadingDialog = loadingDialog // Assign to class property

        // Set the correct text for sign out
        val loadingTitle = loadingDialog.findViewById<TextView>(R.id.loadingTitle)
        val loadingSubtitle = loadingDialog.findViewById<TextView>(R.id.loadingSubtitle)
        loadingTitle?.text = "Signing out..."
        loadingSubtitle?.text = "Please wait while we sign you out"

        // Simulate sign out process with delay
                signOutRunnable = Runnable {
            try {
                // Clear user session data
                val sessionManager = SessionManager(requireContext())
                sessionManager.clearSession()
                
                // Close loading dialog
                loadingDialog.dismiss()
                this.loadingDialog = null // Clear reference
                
                // Navigate back to login screen
                navigateToLogin()
                
            } catch (e: Exception) {
                loadingDialog.dismiss()
                this.loadingDialog = null // Clear reference
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