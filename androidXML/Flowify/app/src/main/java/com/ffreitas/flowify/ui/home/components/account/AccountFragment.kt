package com.ffreitas.flowify.ui.home.components.account

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.FragmentAccountBinding
import com.ffreitas.flowify.ui.home.SharedViewModel
import com.ffreitas.flowify.utils.Constants.APPLICATION_PREFERENCE_NAME
import com.ffreitas.flowify.utils.Constants.USER_PREFERENCE_NAME
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale

class AccountFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: AccountViewModel by viewModels { AccountViewModel.Factory }
    private val shared: SharedViewModel by activityViewModels { SharedViewModel.Factory }

    private val json = Json { ignoreUnknownKeys = true }
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity()
            .getSharedPreferences(APPLICATION_PREFERENCE_NAME, MODE_PRIVATE)
    }

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

        val userEncoded = sharedPreferences.getString(USER_PREFERENCE_NAME, null)

        if (!userEncoded.isNullOrEmpty()) {
            val user = json.decodeFromString(User.serializer(), userEncoded)
            model.setCurrentUser(user)
            updateUserUI(user)
        }

        uiStateUpdate()

        return root
    }

    private fun uiStateUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    when (state) {
                        is UIState.Loading -> {
                            progressDialog.show()
                        }

                        is UIState.Success -> {
                            progressDialog.dismiss()
                            handleSuccess()
                        }

                        is UIState.Error -> {
                            progressDialog.dismiss()
                            handleError(state.message)
                        }

                        is UIState.FileError -> {
                            progressDialog.dismiss()
                            Log.e(TAG, "Failed to load image: ${state.message}")
                            handleErrorMessage(R.string.account_fragment_file_error)
                        }

                        else -> {
                            // Do nothing
                        }
                    }
                }
            }
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
    }

    private fun handleError(error: String) {
        Log.e(TAG, "Error updating user information: $error")
        firebaseAnalytics.logEvent("account_information_updated") {
            param(FirebaseAnalytics.Param.LEVEL, "Account Information")
            param(FirebaseAnalytics.Param.SUCCESS, "false")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "Error")
            param(FirebaseAnalytics.Param.CONTENT, error)
        }
        handleErrorMessage(R.string.account_fragment_submit_error)
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

    private fun updateUserUI(user: User) {
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
        val permissionsUpsideDownCake = arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        val permissionsTiramisu = arrayOf(READ_MEDIA_IMAGES)
        val permissionsOlderVersions = arrayOf(READ_EXTERNAL_STORAGE)

        val hasPermissionGranted = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionsUpsideDownCake.all { permission ->
                    requireContext().checkSelfPermission(permission) == PERMISSION_GRANTED
                }
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                permissionsTiramisu.all {
                    requireContext().checkSelfPermission(it) == PERMISSION_GRANTED
                }
            }

            else -> {
                permissionsOlderVersions.all {
                    requireContext().checkSelfPermission(it) == PERMISSION_GRANTED
                }
            }
        }

        val showRationale = when {
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionsUpsideDownCake.any { shouldShowRequestPermissionRationale(it) }
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                permissionsTiramisu.any { shouldShowRequestPermissionRationale(it) }
            }

            else -> {
                permissionsOlderVersions.any { shouldShowRequestPermissionRationale(it) }
            }
        }

        if (hasPermissionGranted) {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            return
        }
        if (showRationale) {
            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.account_fragment_permission_title))
                .setMessage(getString(R.string.account_fragment_permission_text))
                .setCancelable(true)
                .setIcon(R.drawable.dangerous_24px)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return
        }

        when {

            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                activityResultPermissions.launch(permissionsUpsideDownCake)
            }

            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU -> {
                activityResultPermissions.launch(permissionsTiramisu)
            }

            else -> {
                activityResultPermissions.launch(permissionsOlderVersions)
            }
        }
    }

    private fun handlePhotoSelection(uri: Uri) {
        model.handlePictureSelection(requireContext(), uri)
        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(binding.accountImage)
    }

    private fun onSubmit() {
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
    }
}