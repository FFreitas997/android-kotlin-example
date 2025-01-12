package com.ffreitas.flowify.ui.main.components.account

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.FragmentAccountBinding
import com.ffreitas.flowify.ui.main.HomeUIState
import com.ffreitas.flowify.ui.main.SharedViewModel
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val model by viewModels<AccountViewModel>()
    private val shared by activityViewModels<SharedViewModel>()

    private val activityResultPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var permissionGranted = true
            it.entries.forEach { entry ->
                if (!entry.value)
                    permissionGranted = false
            }
            if (permissionGranted)
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            else
                handleErrorMessage(R.string.account_fragment_permission_denied)
        }

    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri ->
            uri ?: return@registerForActivityResult
            handlePhotoSelection(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressDialog = ProgressDialog(requireActivity())
        firebaseAnalytics = Firebase.analytics

        binding
            .inputName
            .doAfterTextChanged {
                binding.inputNameLayout.error = null
                model.handleChangeName(it)
            }

        binding
            .inputPhone
            .doAfterTextChanged {
                binding.inputPhoneLayout.error = null
                model.handleChangePhone(it)
            }

        binding.accountButton.setOnClickListener { onSubmit() }
        binding.accountImageContainer.setOnClickListener { handleImageSelection() }

        shared.state.observe(viewLifecycleOwner) { state ->
            if (state is HomeUIState.Success)
                model.setCurrentUser(state.data) { currentUserUpdate(it) }
        }

        model.state.observe(viewLifecycleOwner) { handleAccountState(it) }

        return root
    }

    private fun handleAccountState(state: AccountUIState) {
        when (state) {
            is AccountUIState.Loading -> {
                progressDialog.show()
            }

            is AccountUIState.Error -> {
                progressDialog.dismiss()
                handleError(state)
            }

            is AccountUIState.Success -> {
                progressDialog.dismiss()
                handleSuccess()
            }

            else -> { progressDialog.dismiss() }
        }
    }

    private fun handleSuccess() {
        Log.d(TAG, "User information updated successfully.")
        firebaseAnalytics.logEvent("account_information_updated") {
            param(FirebaseAnalytics.Param.LEVEL, "Account Information")
            param(FirebaseAnalytics.Param.SUCCESS, "true")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "User Information")
            param(FirebaseAnalytics.Param.CONTENT, "User information updated successfully.")
        }
        shared.getCurrentUser()
        findNavController().popBackStack()
    }

    private fun handleError(error: AccountError) {
        when (error) {
            is AccountError.SubmitError -> {
                Log.e(TAG, "Error updating user information: ${error.message}")
                firebaseAnalytics.logEvent("account_information_updated") {
                    param(FirebaseAnalytics.Param.LEVEL, "Account Information")
                    param(FirebaseAnalytics.Param.SUCCESS, "false")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "Error")
                    param(FirebaseAnalytics.Param.CONTENT, error.message)
                }
                handleErrorMessage(R.string.account_fragment_submit_error)
            }

            is AccountError.FileError -> {
                Log.e(TAG, "Failed to load image: ${error.message}")
                handleErrorMessage(R.string.account_fragment_file_error)
            }

            else -> {
                Log.e(TAG, "An unknown error occurred.")
            }
        }

    }

    private fun isFormValid(): Boolean {
        var isValid = true

        if (!model.isNameValid()) {
            binding.inputNameLayout.error = getString(R.string.account_fragment_name_error)
            isValid = false
        }

        if (!model.isPhoneValid()) {
            binding.inputPhoneLayout.error = getString(R.string.account_fragment_phone_number_error)
            isValid = false
        }

        return isValid
    }

    private fun currentUserUpdate(user: User) {
        Log.d(TAG, "Updating user information UI.")

        binding.inputName.setText(user.name)
        binding.inputPhone.setText(String.format(Locale.getDefault(), "%d", user.mobile))
        binding.inputEmail.setText(user.email)

        Glide
            .with(this)
            .load(user.picture)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(binding.accountImage)
    }


    @SuppressLint("InlinedApi")
    private fun handleImageSelection() {
        Log.d(TAG, "Handling image selection.")
        if (permissions.all { requireContext().checkSelfPermission(it) == PERMISSION_GRANTED }) {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            return
        }
        if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            handleErrorMessage(R.string.account_fragment_permission_text)
            return
        }
        activityResultPermissions.launch(permissions)
    }

    private fun handlePhotoSelection(uri: Uri) {
        Log.d(TAG, "Handling photo selected from gallery with URI: $uri")
        model.handlePictureSelection(requireContext(), uri)
        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(binding.accountImage)
    }

    private fun onSubmit() {
        Log.d(TAG, "Submitting user information.")
        if (!isFormValid()) return
        model.updateUserInformation()
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(binding.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Account Fragment"

        private val permissions = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                arrayOf(READ_MEDIA_IMAGES)
            }

            else -> {
                arrayOf(READ_EXTERNAL_STORAGE)
            }
        }
    }
}